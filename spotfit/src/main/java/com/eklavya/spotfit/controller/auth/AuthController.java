package com.eklavya.spotfit.controller.auth;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.User;
import com.eklavya.spotfit.dao.OwnerDao;
import com.eklavya.spotfit.service.AuthService;
import com.eklavya.spotfit.service.UserService;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.concurrent.CompletableFuture;

/**
 * AuthController manages authentication and user profile synchronization for SpotFit users.
 * Coordinates AuthService, UserService, and AppSession without JavaFX UI dependencies.
 */
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final OwnerDao ownerDao;

    public AuthController() {
        this(new AuthService(), new UserService(), new OwnerDao());
    }

    public AuthController(AuthService authService) {
        this(authService, new UserService(), new OwnerDao());
    }

    public AuthController(AuthService authService, UserService userService) {
        this(authService, userService, new OwnerDao());
    }

    public AuthController(AuthService authService, UserService userService, OwnerDao ownerDao) {
        this.authService = (authService != null) ? authService : new AuthService();
        this.userService = (userService != null) ? userService : new UserService();
        this.ownerDao = (ownerDao != null) ? ownerDao : new OwnerDao();
    }

    public CompletableFuture<AuthResult> registerUser(
            String name,
            String email,
            String password,
            String confirmPassword,
            boolean termsAccepted) {

        if (ValidationUtil.isBlank(name)) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("NAME_REQUIRED", "Please enter your full name."));
        }
        if (!ValidationUtil.passwordsMatch(password, confirmPassword)) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("PASSWORDS_DO_NOT_MATCH", "Passwords do not match."));
        }
        if (!termsAccepted) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("TERMS_NOT_ACCEPTED", "You must agree to the Terms & Conditions."));
        }

        OperationResult<Void> credentialValidation = ValidationUtil.validateCredentials(email, password);
        if (credentialValidation.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure(credentialValidation.getErrorCode(), credentialValidation.getMessage()));
        }

        return authService.signUp(email, password)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        AppSession.startSession(
                                result.getUserId(),
                                result.getEmail(),
                                name.trim(),
                                UserRole.USER,
                                result.getIdToken(),
                                result.getRefreshToken(),
                                result.getExpiresInSeconds()
                        );
                    }
                    return result;
                });
    }

    public CompletableFuture<OperationResult<User>> registerUserWithProfile(
            String name,
            String email,
            String phone,
            String password,
            String confirmPassword,
            boolean termsAccepted) {

        if (ValidationUtil.isBlank(name)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("NAME_REQUIRED", "Please enter your full name."));
        }
        if (!ValidationUtil.passwordsMatch(password, confirmPassword)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("PASSWORDS_DO_NOT_MATCH", "Passwords do not match."));
        }
        if (!termsAccepted) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("TERMS_NOT_ACCEPTED", "You must agree to the Terms & Conditions."));
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
                            UserRole.USER,
                            authRes.getIdToken(),
                            authRes.getRefreshToken(),
                            authRes.getExpiresInSeconds()
                    );

                    // Cross-Role Protection: Check if Owner profile exists
                    return ownerDao.getOwner(authRes.getUserId(), authRes.getIdToken())
                            .thenCompose(ownerRes -> {
                                if (ownerRes.isSuccess() && ownerRes.getData() != null) {
                                    AppSession.clearSession();
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(OperationCode.ROLE_MISMATCH,
                                                    "This account is registered as a SpotFit Owner. Please use Owner Login."));
                                }
                                if (ownerRes.isFailure() && !ownerRes.isNotFound()) {
                                    AppSession.clearSession();
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(ownerRes.getErrorCode(),
                                                    "Could not verify account role: " + ownerRes.getMessage()));
                                }

                                return userService.createInitialProfile(
                                        authRes.getUserId(),
                                        name,
                                        email,
                                        phone,
                                        authRes.getIdToken()
                                ).thenApply(userRes -> {
                                    if (userRes.isSuccess()) {
                                        return OperationResult.success(userRes.getData(), "Account and profile created successfully.");
                                    } else {
                                        AppSession.clearSession();
                                        return OperationResult.failure(userRes.getErrorCode(),
                                                "Profile setup failed: " + userRes.getMessage());
                                    }
                                });
                            });
                });
    }

    public CompletableFuture<AuthResult> loginUser(String email, String password) {
        OperationResult<Void> credentialValidation = ValidationUtil.validateCredentials(email, password);
        if (credentialValidation.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure(credentialValidation.getErrorCode(), credentialValidation.getMessage()));
        }

        return authService.signIn(email, password)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        String displayName = deriveDisplayName(result.getEmail());
                        AppSession.startSession(
                                result.getUserId(),
                                result.getEmail(),
                                displayName,
                                UserRole.USER,
                                result.getIdToken(),
                                result.getRefreshToken(),
                                result.getExpiresInSeconds()
                        );
                    }
                    return result;
                });
    }

    public CompletableFuture<OperationResult<User>> loginUserWithProfile(String email, String password) {
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
                            UserRole.USER,
                            authRes.getIdToken(),
                            authRes.getRefreshToken(),
                            authRes.getExpiresInSeconds()
                    );

                    return userService.getUser(authRes.getUserId(), authRes.getIdToken())
                            .thenCompose(userRes -> {
                                if (userRes.isSuccess()) {
                                    User user = userRes.getData();

                                    if (!authRes.getUserId().equals(user.getUserId())) {
                                        AppSession.clearSession();
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure(OperationCode.ROLE_MISMATCH, "Profile identity mismatch."));
                                    }
                                    if (!UserRole.USER.equals(user.getRole())) {
                                        AppSession.clearSession();
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure(OperationCode.ROLE_MISMATCH,
                                                        "This account is registered as a SpotFit Owner. Please use Owner Login."));
                                    }
                                    if (!user.isActive()) {
                                        AppSession.clearSession();
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure(OperationCode.PERMISSION_DENIED, "Your user account is inactive."));
                                    }

                                    AppSession.updateProfile(user.getName(), user.getEmail());

                                    if (authRes.isEmailVerified() && !user.isEmailVerified()) {
                                        return userService.markEmailVerified(user.getUserId(), true, authRes.getIdToken())
                                                .thenApply(updatedRes -> {
                                                    if (updatedRes.isSuccess()) {
                                                        return OperationResult.success(updatedRes.getData());
                                                    }
                                                    return OperationResult.success(user);
                                                });
                                    }

                                    return CompletableFuture.completedFuture(OperationResult.success(user));
                                } else if (userRes.isNotFound()) {
                                    // Cross-Role Check: Before creating missing /users/{uid}, check if /owners/{uid} exists
                                    return ownerDao.getOwner(authRes.getUserId(), authRes.getIdToken())
                                            .thenCompose(ownerRes -> {
                                                if (ownerRes.isSuccess() && ownerRes.getData() != null) {
                                                    AppSession.clearSession();
                                                    return CompletableFuture.completedFuture(
                                                            OperationResult.failure(OperationCode.ROLE_MISMATCH,
                                                                    "This account is registered as a SpotFit Owner. Please use Owner Login."));
                                                }
                                                if (ownerRes.isFailure() && !ownerRes.isNotFound()) {
                                                    AppSession.clearSession();
                                                    return CompletableFuture.completedFuture(
                                                            OperationResult.failure(ownerRes.getErrorCode(),
                                                                    "Could not verify owner role: " + ownerRes.getMessage()));
                                                }

                                                // Missing document recovery for legacy Customer account
                                                return userService.createInitialProfile(
                                                        authRes.getUserId(),
                                                        displayName,
                                                        authRes.getEmail(),
                                                        null,
                                                        authRes.getIdToken()
                                                ).thenApply(recoveryRes -> {
                                                    if (recoveryRes.isSuccess()) {
                                                        User recovered = recoveryRes.getData();
                                                        AppSession.updateProfile(recovered.getName(), recovered.getEmail());
                                                        return OperationResult.success(recovered, "Profile initialized for existing account.");
                                                    } else {
                                                        AppSession.clearSession();
                                                        return OperationResult.failure(recoveryRes.getErrorCode(),
                                                                "User profile could not be loaded or initialized: " + recoveryRes.getMessage());
                                                    }
                                                });
                                            });
                                } else {
                                    // Real backend error on /users/{uid} read (e.g. PERMISSION_DENIED or NETWORK_ERROR)
                                    AppSession.clearSession();
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(userRes.getErrorCode(),
                                                    "Failed to load user profile: " + userRes.getMessage()));
                                }
                            });
                });
    }

    public CompletableFuture<OperationResult<Void>> sendVerificationEmail() {
        if (!AppSession.isAuthenticated() || ValidationUtil.isBlank(AppSession.getIdToken())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SESSION_REQUIRED", "Please sign in again before requesting verification."));
        }
        return authService.sendEmailVerification(AppSession.getIdToken());
    }

    public CompletableFuture<AuthResult> checkCurrentUserEmailVerification() {
        if (!AppSession.isAuthenticated() || ValidationUtil.isBlank(AppSession.getIdToken())) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("SESSION_REQUIRED", "Please sign in again before checking verification."));
        }

        CompletableFuture<String> tokenFuture;
        if (AppSession.shouldRefreshToken() && !ValidationUtil.isBlank(AppSession.getRefreshToken())) {
            tokenFuture = authService.refreshToken(AppSession.getRefreshToken())
                    .thenApply(refreshResult -> {
                        if (refreshResult.isSuccess()) {
                            AppSession.updateTokens(
                                    refreshResult.getIdToken(),
                                    refreshResult.getRefreshToken(),
                                    refreshResult.getExpiresInSeconds()
                            );
                            return refreshResult.getIdToken();
                        }
                        return AppSession.getIdToken();
                    });
        } else {
            tokenFuture = CompletableFuture.completedFuture(AppSession.getIdToken());
        }

        return tokenFuture.thenCompose(idToken -> authService.checkEmailVerification(idToken));
    }

    public CompletableFuture<OperationResult<Void>> sendPasswordReset(String email) {
        OperationResult<Void> emailValidation = ValidationUtil.validateEmail(email);
        if (emailValidation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(emailValidation.getErrorCode(), emailValidation.getMessage()));
        }
        return authService.sendPasswordReset(email);
    }

    public void logout() {
        AppSession.clearSession();
    }

    private String deriveDisplayName(String email) {
        if (ValidationUtil.isBlank(email) || !email.contains("@")) {
            return "User";
        }
        String local = email.substring(0, email.indexOf('@')).trim();
        if (local.isEmpty()) {
            return "User";
        }
        return Character.toUpperCase(local.charAt(0)) + (local.length() > 1 ? local.substring(1) : "");
    }
}
