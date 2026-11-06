package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.OwnerDao;
import com.eklavya.spotfit.dao.UserDao;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.service.AuthService;
import com.eklavya.spotfit.service.InvitationService;
import com.eklavya.spotfit.service.ManagerService;
import com.eklavya.spotfit.session.AppSession;

import java.util.concurrent.CompletableFuture;

/**
 * Controller managing Manager Authentication, Registration, Token Refresh after Email Verification,
 * and Atomic Account Activation.
 */
public class ManagerAuthController {

    private final AuthService authService;
    private final ManagerService managerService;
    private final InvitationService invitationService;
    private final UserDao userDao;
    private final OwnerDao ownerDao;

    public ManagerAuthController() {
        this(new AuthService(), new ManagerService(), new InvitationService(), new UserDao(), new OwnerDao());
    }

    public ManagerAuthController(
            AuthService authService,
            ManagerService managerService,
            InvitationService invitationService) {
        this(authService, managerService, invitationService, new UserDao(), new OwnerDao());
    }

    public ManagerAuthController(
            AuthService authService,
            ManagerService managerService,
            InvitationService invitationService,
            UserDao userDao,
            OwnerDao ownerDao) {
        this.authService = (authService != null) ? authService : new AuthService();
        this.managerService = (managerService != null) ? managerService : new ManagerService();
        this.invitationService = (invitationService != null) ? invitationService : new InvitationService();
        this.userDao = (userDao != null) ? userDao : new UserDao();
        this.ownerDao = (ownerDao != null) ? ownerDao : new OwnerDao();
    }

    /**
     * Step 1 of Manager Registration:
     * Validates form fields, verifies invitation readiness, signs up user in Firebase Auth, and sends verification email.
     */
    public CompletableFuture<OperationResult<AuthResult>> registerManager(
            String invitationCode,
            String name,
            String email,
            String phone,
            String password,
            String confirmPassword) {

        if (password == null || !password.equals(confirmPassword)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("PASSWORD_MISMATCH", "Passwords do not match."));
        }

        // 1. Invitation-first validation BEFORE Auth signup
        return invitationService.validateInvitationForRegistration(invitationCode, email)
                .thenCompose(invRes -> {
                    if (invRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(invRes.getErrorCode(), invRes.getMessage()));
                    }

                    // 2. Invitation is valid -> Proceed to Firebase Auth signup
                    return authService.signUp(email, password)
                            .thenCompose(authResult -> {
                                if (!authResult.isSuccess()) {
                                    String code = authResult.getErrorCode();
                                    String msg = authResult.getMessage();
                                    if ("EMAIL_EXISTS".equalsIgnoreCase(code) ||
                                            "EMAIL_ALREADY_IN_USE".equalsIgnoreCase(code) ||
                                            OperationCode.EMAIL_ALREADY_EXISTS.equalsIgnoreCase(code) ||
                                            (msg != null && (msg.contains("EMAIL_EXISTS") || msg.contains("EMAIL_ALREADY_IN_USE")))) {
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure(
                                                        OperationCode.EMAIL_ALREADY_EXISTS,
                                                        "An authentication account already exists for this email. Sign in to continue accepting this Manager invitation."
                                                )
                                        );
                                    }
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(code, msg));
                                }

                                // 3. Send email verification link
                                return authService.sendEmailVerification(authResult.getIdToken())
                                        .thenApply(sendRes -> OperationResult.success(authResult,
                                                "Registration successful! Verification email sent to " + email + ". Please verify before activating account."));
                            });
                });
    }

    /**
     * Recovery flow: Authenticates existing Auth user, checks cross-role profiles, and continues invitation acceptance.
     */
    public CompletableFuture<OperationResult<Manager>> loginManagerAndContinueInvitation(
            String email,
            String password,
            String invitationCode) {

        // 1. Validate invitation readiness
        return invitationService.validateInvitationForRegistration(invitationCode, email)
                .thenCompose(invRes -> {
                    if (invRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(invRes.getErrorCode(), invRes.getMessage()));
                    }

                    ManagerInvitation invitation = invRes.getData();

                    // 2. Authenticate existing account
                    return authService.signIn(email, password)
                            .thenCompose(authResult -> {
                                if (!authResult.isSuccess()) {
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure(authResult.getErrorCode(), authResult.getMessage()));
                                }

                                String uid = authResult.getUserId();
                                String tokenEmail = authResult.getEmail();
                                String idToken = authResult.getIdToken();

                                if (tokenEmail == null || !tokenEmail.trim().equalsIgnoreCase(invitation.getManagerEmail().trim())) {
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure("EMAIL_MISMATCH", "Authenticated email (" + tokenEmail + ") does not match invitation email (" + invitation.getManagerEmail() + ")."));
                                }

                                // 3. Perform cross-role checks (/users/{uid}, /owners/{uid}, /managers/{uid})
                                return userDao.getUser(uid, idToken)
                                        .thenCompose(userRes -> {
                                            if (userRes.isSuccess()) {
                                                return CompletableFuture.completedFuture(
                                                        OperationResult.<Manager>failure("REJECTED_CUSTOMER_ACCOUNT",
                                                                "This account is registered as a SpotFit Customer and cannot accept a Manager invitation."));
                                            }

                                            return ownerDao.getOwner(uid, idToken)
                                                    .thenCompose(ownerRes -> {
                                                        if (ownerRes.isSuccess()) {
                                                            return CompletableFuture.completedFuture(
                                                                    OperationResult.<Manager>failure("REJECTED_OWNER_ACCOUNT",
                                                                            "This account is registered as a SpotFit Owner and cannot accept a Manager invitation."));
                                                        }

                                                        return managerService.getManagerProfile(uid, idToken)
                                                                .thenCompose(mgrRes -> {
                                                                    if (mgrRes.isSuccess()) {
                                                                        Manager existingMgr = mgrRes.getData();
                                                                        if (!invitation.getOwnerId().equals(existingMgr.getOwnerId())) {
                                                                            return CompletableFuture.completedFuture(
                                                                                    OperationResult.<Manager>failure("MULTIPLE_OWNERS_UNSUPPORTED",
                                                                                            "This account is already an active Manager for another Owner. Multiple-owner Manager assignments are not yet supported."));
                                                                        }
                                                                        return managerService.activateManagerAccountAtomically(
                                                                                uid, existingMgr.getName(), tokenEmail, existingMgr.getPhone(),
                                                                                true, invitationCode, idToken
                                                                        );
                                                                    }

                                                                    // 4. Orphaned Auth account -> Check email verification status
                                                                    if (!authResult.isEmailVerified()) {
                                                                        return CompletableFuture.completedFuture(
                                                                                OperationResult.<Manager>failure("EMAIL_NOT_VERIFIED",
                                                                                        "Please verify your email address before completing invitation acceptance. Check your inbox for the verification link."));
                                                                    }

                                                                    // 5. Email verified -> Atomically create Manager profile & redeem invitation
                                                                    String name = (tokenEmail.contains("@")) ? tokenEmail.substring(0, tokenEmail.indexOf('@')) : tokenEmail;
                                                                    return managerService.activateManagerAccountAtomically(
                                                                            uid, name, tokenEmail, "", true, invitationCode, idToken
                                                                    );
                                                                });
                                                    });
                                        });
                            });
                });
    }

    /**
     * Complete Post-Verification Manager Activation Sequence.
     * 1. Validates temporary Manager session (authResult, invitationCode).
     * 2. Refreshes Firebase ID token (guaranteeing fresh credentials).
     * 3. Calls Firebase accounts:lookup to confirm authoritative emailVerified == true.
     * 4. Updates AppSession with fresh ID and refresh tokens.
     * 5. Re-fetches invitation using authenticated ID token.
     * 6. Re-validates invitation status and recipient email.
     * 7. Performs cross-role checks (/users/{uid} and /owners/{uid}).
     * 8. Atomically creates Manager profile and updates invitation status to ACCEPTED (or handles idempotent recovery).
     */
    public CompletableFuture<OperationResult<Manager>> checkVerificationAndActivateManager(
            AuthResult authResult,
            String invitationCode,
            String name,
            String phone) {

        if (authResult == null || authResult.getRefreshToken() == null || authResult.getRefreshToken().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("MISSING_RECOVERY_CONTEXT",
                            "Your invitation activation session is missing or expired. Please sign in and continue your invitation again."));
        }

        String normalizedCode = ManagerInvitation.normalizeCode(invitationCode);
        if (normalizedCode == null || normalizedCode.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("MISSING_RECOVERY_CONTEXT",
                            "Your invitation activation session is missing or expired. Please sign in and continue your invitation again."));
        }

        // 1. Refresh ID token first
        return authService.refreshToken(authResult.getRefreshToken())
                .thenCompose(refreshedAuth -> {
                    if (!refreshedAuth.isSuccess()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(refreshedAuth.getErrorCode(),
                                        "Failed to refresh authentication session: " + refreshedAuth.getMessage()));
                    }

                    String freshToken = refreshedAuth.getIdToken();
                    String freshRefreshToken = refreshedAuth.getRefreshToken();
                    String uid = refreshedAuth.getUserId();

                    // 2. Perform authoritative Firebase lookup (accounts:lookup)
                    return authService.checkEmailVerification(freshToken)
                            .thenCompose(accountInfo -> {
                                if (!accountInfo.isEmailVerified()) {
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure("EMAIL_NOT_VERIFIED",
                                                    "Your email is not verified yet. Open the link sent to your email and try again."));
                                }

                                String email = (accountInfo.getEmail() != null && !accountInfo.getEmail().isBlank())
                                        ? accountInfo.getEmail() : refreshedAuth.getEmail();

                                if (email == null || email.isBlank()) {
                                    return CompletableFuture.completedFuture(
                                            OperationResult.failure("INVALID_EMAIL", "Authenticated user email cannot be empty."));
                                }

                                // 3. Re-fetch invitation using authenticated ID token
                                return invitationService.getInvitationByCode(normalizedCode, freshToken)
                                        .thenCompose(invRes -> {
                                            if (invRes.isFailure()) {
                                                return CompletableFuture.completedFuture(
                                                        OperationResult.failure(invRes.getErrorCode(), invRes.getMessage()));
                                            }

                                            // 4. Perform cross-role checks
                                            return userDao.getUser(uid, freshToken)
                                                    .thenCompose(userRes -> {
                                                        if (userRes.isSuccess()) {
                                                            return CompletableFuture.completedFuture(
                                                                    OperationResult.<Manager>failure("REJECTED_CUSTOMER_ACCOUNT",
                                                                            "This account is registered as a SpotFit Customer and cannot accept a Manager invitation."));
                                                        }

                                                        return ownerDao.getOwner(uid, freshToken)
                                                                .thenCompose(ownerRes -> {
                                                                    if (ownerRes.isSuccess()) {
                                                                        return CompletableFuture.completedFuture(
                                                                                OperationResult.<Manager>failure("REJECTED_OWNER_ACCOUNT",
                                                                                        "This account is registered as a SpotFit Owner and cannot accept a Manager invitation."));
                                                                    }

                                                                    // 5. Update AppSession
                                                                    AppSession.startSession(
                                                                            uid, email, name, UserRole.MANAGER,
                                                                            freshToken, freshRefreshToken, refreshedAuth.getExpiresInSeconds()
                                                                    );

                                                                    // 6. Atomically activate manager account
                                                                    return managerService.activateManagerAccountAtomically(
                                                                            uid, name, email, phone, true, normalizedCode, freshToken
                                                                    );
                                                                });
                                                    });
                                        });
                            });
                });
    }

    public CompletableFuture<OperationResult<Manager>> verifyEmailAndActivate(
            AuthResult authResult,
            String invitationCode,
            String name,
            String phone) {
        return checkVerificationAndActivateManager(authResult, invitationCode, name, phone);
    }

    public CompletableFuture<OperationResult<Void>> sendVerificationEmail(AuthResult authResult) {
        if (authResult == null || authResult.getIdToken() == null || authResult.getIdToken().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_AUTH_STATE", "Cannot send verification email: missing session token."));
        }
        return authService.sendEmailVerification(authResult.getIdToken());
    }

    /**
     * Manager Login:
     * Authenticates with Firebase, checks role == MANAGER, verifies active status, starts AppSession.
     */
    public CompletableFuture<OperationResult<Manager>> loginManager(String email, String password) {
        return authService.signIn(email, password)
                .thenCompose(authResult -> {
                    if (!authResult.isSuccess()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(authResult.getErrorCode(), authResult.getMessage()));
                    }

                    if (!authResult.isEmailVerified()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure("EMAIL_NOT_VERIFIED", "Please verify your email address before logging in."));
                    }

                    String uid = authResult.getUserId();
                    String freshToken = authResult.getIdToken();

                    // Load /managers/{uid}
                    return managerService.getManagerProfile(uid, freshToken)
                            .thenApply(mgrRes -> {
                                if (mgrRes.isFailure()) {
                                    if ("NOT_FOUND".equals(mgrRes.getErrorCode())) {
                                        return OperationResult.failure("INCOMPLETE_ACTIVATION",
                                                "Manager account registration is incomplete. Please redeem your invitation code to finish setup.");
                                    }
                                    return OperationResult.failure(mgrRes.getErrorCode(), mgrRes.getMessage());
                                }

                                Manager manager = mgrRes.getData();
                                if (!UserRole.MANAGER.equalsIgnoreCase(manager.getRole())) {
                                    return OperationResult.failure("ROLE_MISMATCH", "Account is not registered as a Manager.");
                                }
                                if (!manager.isActive()) {
                                    return OperationResult.failure("ACCOUNT_DEACTIVATED", "Manager account has been deactivated by the Owner.");
                                }

                                // Start Manager session
                                AppSession.startSession(
                                        manager.getManagerId(),
                                        manager.getEmail(),
                                        manager.getName(),
                                        UserRole.MANAGER,
                                        freshToken,
                                        authResult.getRefreshToken(),
                                        authResult.getExpiresInSeconds()
                                );

                                return OperationResult.success(manager, "Login successful.");
                            });
                });
    }

    public CompletableFuture<OperationResult<Void>> sendPasswordReset(String email) {
        return authService.sendPasswordReset(email);
    }

    public void logout() {
        AppSession.clearSession();
    }
}
