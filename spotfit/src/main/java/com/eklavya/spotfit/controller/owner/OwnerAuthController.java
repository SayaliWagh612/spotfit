package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.UserDao;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Owner;
import com.eklavya.spotfit.service.AuthService;
import com.eklavya.spotfit.service.OwnerService;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.util.AuthenticatedExecutor;
import com.eklavya.spotfit.util.ValidationUtil;
import com.eklavya.spotfit.view.owner.OwnerShell;

import java.util.concurrent.CompletableFuture;

/**
 * Controller coordinating Owner authentication, session management,
 * and Firestore owner profile routing.
 */
public class OwnerAuthController {

    private final AuthService authService;
    private final OwnerService ownerService;
    private final UserDao userDao;

    public OwnerAuthController() {
        this(new AuthService(), new OwnerService(), new UserDao());
    }

    public OwnerAuthController(AuthService authService, OwnerService ownerService, UserDao userDao) {
        this.authService = (authService != null) ? authService : new AuthService();
        this.ownerService = (ownerService != null) ? ownerService : new OwnerService();
        this.userDao = (userDao != null) ? userDao : new UserDao();
    }

    public CompletableFuture<OperationResult<Owner>> registerOwnerWithProfile(
            String name,
            String email,
            String phone,
            String password,
            String confirmPassword) {

        if (ValidationUtil.isBlank(name)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("NAME_REQUIRED", "Please enter your full name."));
        }
        if (!ValidationUtil.passwordsMatch(password, confirmPassword)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("PASSWORDS_DO_NOT_MATCH", "Passwords do not match."));
        }

        OperationResult<Void> credentialValidation = ValidationUtil.validateCredentials(email, password);
        if (credentialValidation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(credentialValidation.getErrorCode(), credentialValidation.getMessage()));
        }

        return authService.signUp(email, password)
                .thenCompose(authRes -> {
                    if (authRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
                    }

                    AppSession.startSession(
                            authRes.getUserId(),
                            authRes.getEmail(),
                            name.trim(),
                            UserRole.OWNER,
                            authRes.getIdToken(),
                            authRes.getRefreshToken(),
                            authRes.getExpiresInSeconds()
                    );

                    // Cross-Role Protection: Check if Customer profile exists
                    return userDao.getUser(authRes.getUserId(), authRes.getIdToken())
                            .thenCompose(userRes -> {
                                if (userRes.isSuccess() && userRes.getData() != null) {
                                    AppSession.clearSession();
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(OperationCode.ROLE_MISMATCH,
                                                    "This account is registered as a SpotFit Customer. Please use Customer Login."));
                                }
                                if (userRes.isFailure() && !userRes.isNotFound()) {
                                    AppSession.clearSession();
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(userRes.getErrorCode(),
                                                    "Could not verify account role: " + userRes.getMessage()));
                                }

                                // Check if owner profile already exists
                                return ownerService.getOwner(authRes.getUserId(), authRes.getIdToken())
                                        .thenCompose(existingOwnerRes -> {
                                            if (existingOwnerRes.isSuccess() && existingOwnerRes.getData() != null) {
                                                Owner existing = existingOwnerRes.getData();
                                                authService.sendEmailVerification(authRes.getIdToken());
                                                return CompletableFuture.completedFuture(
                                                        OperationResult.success(existing, "Owner account exists. Please verify your email."));
                                            }
                                            if (existingOwnerRes.isFailure() && !existingOwnerRes.isNotFound()) {
                                                AppSession.clearSession();
                                                return CompletableFuture.completedFuture(
                                                        OperationResult.failure(existingOwnerRes.getErrorCode(),
                                                                "Could not check existing owner profile: " + existingOwnerRes.getMessage()));
                                            }

                                            return ownerService.initializeOwnerProfile(
                                                    authRes.getUserId(),
                                                    name,
                                                    authRes.getEmail(),
                                                    phone,
                                                    authRes.isEmailVerified(),
                                                    authRes.getIdToken()
                                            ).thenCompose(ownerRes -> {
                                                if (ownerRes.isSuccess()) {
                                                    authService.sendEmailVerification(authRes.getIdToken());
                                                    return CompletableFuture.completedFuture(
                                                            OperationResult.success(ownerRes.getData(), "Owner account created. Please verify your email."));
                                                } else {
                                                    AppSession.clearSession();
                                                    return CompletableFuture.completedFuture(
                                                            OperationResult.failure(ownerRes.getErrorCode(),
                                                                    "Owner profile setup failed: " + ownerRes.getMessage()));
                                                }
                                            });
                                        });
                            });
                });
    }

    public CompletableFuture<OperationResult<Owner>> loginOwnerWithProfile(String email, String password) {
        OperationResult<Void> credentialValidation = ValidationUtil.validateCredentials(email, password);
        if (credentialValidation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(credentialValidation.getErrorCode(), credentialValidation.getMessage()));
        }

        return authService.signIn(email, password)
                .thenCompose(authRes -> {
                    if (authRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
                    }

                    if (!authRes.hasUserId() || !authRes.hasTokens()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                                        "Authentication succeeded but user identity token was missing."));
                    }

                    String displayName = deriveDisplayName(authRes.getEmail());
                    AppSession.startSession(
                            authRes.getUserId(),
                            authRes.getEmail(),
                            displayName,
                            UserRole.OWNER,
                            authRes.getIdToken(),
                            authRes.getRefreshToken(),
                            authRes.getExpiresInSeconds()
                    );

                    return ownerService.getOwner(authRes.getUserId(), authRes.getIdToken())
                            .thenCompose(ownerRes -> {
                                if (ownerRes.isSuccess()) {
                                    Owner owner = ownerRes.getData();

                                    if (!authRes.getUserId().equals(owner.getOwnerId())) {
                                        AppSession.clearSession();
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure(OperationCode.ROLE_MISMATCH, "Identity mismatch."));
                                    }
                                    if (!UserRole.OWNER.equals(owner.getRole())) {
                                        AppSession.clearSession();
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure(OperationCode.ROLE_MISMATCH,
                                                        "This account is not registered as a SpotFit Owner."));
                                    }
                                    if (!owner.isActive()) {
                                        AppSession.clearSession();
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure(OperationCode.PERMISSION_DENIED, "Your owner account is inactive."));
                                    }

                                    AppSession.updateProfile(owner.getName(), owner.getEmail());

                                    if (authRes.isEmailVerified() && !owner.isEmailVerified()) {
                                        return ownerService.markEmailVerified(owner.getOwnerId(), authRes.getIdToken())
                                                .thenApply(updatedRes -> {
                                                    if (updatedRes.isSuccess()) {
                                                        return OperationResult.success(updatedRes.getData());
                                                    }
                                                    return OperationResult.success(owner);
                                                });
                                    }

                                    return CompletableFuture.completedFuture(OperationResult.success(owner));
                                } else if (ownerRes.isNotFound()) {
                                    // Cross-Role Check: If missing in /owners, check if present in /users
                                    return userDao.getUser(authRes.getUserId(), authRes.getIdToken())
                                            .thenCompose(userRes -> {
                                                if (userRes.isSuccess() && userRes.getData() != null) {
                                                    AppSession.clearSession();
                                                    return CompletableFuture.completedFuture(
                                                            OperationResult.failure(OperationCode.ROLE_MISMATCH,
                                                                    "This account is registered as a SpotFit Customer. Please use Customer Login."));
                                                }
                                                if (userRes.isFailure() && !userRes.isNotFound()) {
                                                    AppSession.clearSession();
                                                    return CompletableFuture.completedFuture(
                                                            OperationResult.failure(userRes.getErrorCode(),
                                                                    "Could not verify user role: " + userRes.getMessage()));
                                                }

                                                // Legacy owner profile initialization for orphaned Auth account
                                                return ownerService.initializeOwnerProfile(
                                                        authRes.getUserId(),
                                                        displayName,
                                                        authRes.getEmail(),
                                                        "",
                                                        authRes.isEmailVerified(),
                                                        authRes.getIdToken()
                                                ).thenApply(recoveryRes -> {
                                                    if (recoveryRes.isSuccess()) {
                                                        Owner recovered = recoveryRes.getData();
                                                        AppSession.updateProfile(recovered.getName(), recovered.getEmail());
                                                        return OperationResult.success(recovered, "Owner profile initialized for existing account.");
                                                    } else {
                                                        AppSession.clearSession();
                                                        return OperationResult.failure(recoveryRes.getErrorCode(),
                                                                "Owner profile initialization failed: " + recoveryRes.getMessage());
                                                    }
                                                });
                                            });
                                } else {
                                    // Real backend error on /owners/{uid} read (e.g., PERMISSION_DENIED or NETWORK_ERROR)
                                    AppSession.clearSession();
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(ownerRes.getErrorCode(),
                                                    "Failed to load owner profile: " + ownerRes.getMessage()));
                                }
                            });
                });
    }

    public CompletableFuture<OperationResult<Void>> sendVerificationEmail() {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }
        return AuthenticatedExecutor.execute(token -> authService.sendEmailVerification(token));
    }

    public CompletableFuture<AuthResult> checkOwnerEmailVerification() {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }

        return authService.checkEmailVerification(AppSession.getIdToken())
                .thenCompose(lookupRes -> {
                    if (lookupRes.isSuccess() && lookupRes.isEmailVerified()) {
                        return authService.refreshToken(AppSession.getRefreshToken())
                                .thenCompose(refreshRes -> {
                                    if (refreshRes.isSuccess()) {
                                        AppSession.updateTokens(
                                                refreshRes.getIdToken(),
                                                refreshRes.getRefreshToken(),
                                                refreshRes.getExpiresInSeconds()
                                        );
                                    }

                                    String activeToken = AppSession.getIdToken();
                                    return ownerService.markEmailVerified(AppSession.getUserId(), activeToken)
                                            .thenApply(ownerRes -> lookupRes);
                                });
                    }
                    return CompletableFuture.completedFuture(lookupRes);
                });
    }

    public CompletableFuture<OperationResult<Void>> sendPasswordReset(String email) {
        return authService.sendPasswordReset(email);
    }

    public CompletableFuture<OperationResult<Owner>> loadCurrentOwner() {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }
        return AuthenticatedExecutor.execute(token -> ownerService.getOwner(AppSession.getUserId(), token));
    }

    public CompletableFuture<OperationResult<Owner>> updateOwnerProfile(
            String name,
            String phone,
            String city,
            String profileImageUrl) {

        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }
        return AuthenticatedExecutor.execute(token -> ownerService.updateBasicProfile(
                AppSession.getUserId(), name, phone, city, profileImageUrl, token));
    }

    public CompletableFuture<OperationResult<Owner>> completeOwnerProfile() {
        if (!AppSession.isAuthenticated() || !AppSession.hasIdToken() || AppSession.getUserId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again."));
        }
        return AuthenticatedExecutor.execute(token -> ownerService.completeProfile(AppSession.getUserId(), token));
    }

    public void logout() {
        AppSession.clearSession();
        OwnerShell.resetInstance();
    }

    private String deriveDisplayName(String email) {
        if (email == null || email.isBlank()) {
            return "Owner";
        }
        String localPart = email.split("@")[0];
        if (localPart.isEmpty()) {
            return "Owner";
        }
        return Character.toUpperCase(localPart.charAt(0)) + localPart.substring(1);
    }
}
