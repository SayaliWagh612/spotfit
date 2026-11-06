package com.eklavya.spotfit.service;

import com.eklavya.spotfit.dao.AuthDao;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.concurrent.CompletableFuture;

/**
 * High-level authentication service coordinating validation and AuthDao interactions.
 */
public class AuthService {

    private final AuthDao authDao;

    public AuthService() {
        this(new AuthDao());
    }

    public AuthService(AuthDao authDao) {
        this.authDao = authDao;
    }

    public CompletableFuture<AuthResult> signUp(String email, String password) {
        OperationResult<Void> validation = ValidationUtil.validateCredentials(email, password);
        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        return authDao.signUp(normalizedEmail, password)
                .thenCompose(signUpResult -> {
                    if (!signUpResult.isSuccess()) {
                        return CompletableFuture.completedFuture(signUpResult);
                    }

                    return authDao.getAccountInfo(signUpResult.getIdToken())
                            .thenApply(lookupResult -> {
                                if (!lookupResult.isSuccess()) {
                                    return signUpResult;
                                }

                                return AuthResult.success(
                                        (lookupResult.getUserId() != null && !lookupResult.getUserId().isBlank())
                                                ? lookupResult.getUserId() : signUpResult.getUserId(),
                                        (lookupResult.getEmail() != null && !lookupResult.getEmail().isBlank())
                                                ? lookupResult.getEmail() : signUpResult.getEmail(),
                                        signUpResult.getIdToken(),
                                        signUpResult.getRefreshToken(),
                                        signUpResult.getExpiresInSeconds(),
                                        lookupResult.isEmailVerified(),
                                        signUpResult.getMessage()
                                );
                            });
                });
    }

    public CompletableFuture<AuthResult> signIn(String email, String password) {
        OperationResult<Void> validation = ValidationUtil.validateCredentials(email, password);
        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        String normalizedEmail = ValidationUtil.normalizeEmail(email);

        return authDao.signIn(normalizedEmail, password)
                .thenCompose(signInResult -> {
                    if (!signInResult.isSuccess()) {
                        return CompletableFuture.completedFuture(signInResult);
                    }

                    return authDao.getAccountInfo(signInResult.getIdToken())
                            .thenApply(lookupResult -> {
                                if (!lookupResult.isSuccess()) {
                                    return lookupResult;
                                }

                                return AuthResult.success(
                                        lookupResult.getUserId(),
                                        lookupResult.getEmail(),
                                        signInResult.getIdToken(),
                                        signInResult.getRefreshToken(),
                                        signInResult.getExpiresInSeconds(),
                                        lookupResult.isEmailVerified(),
                                        signInResult.getMessage()
                                );
                            });
                });
    }

    public CompletableFuture<OperationResult<Void>> sendEmailVerification(String idToken) {
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "Authentication token is required."));
        }
        return authDao.sendEmailVerification(idToken);
    }

    public CompletableFuture<AuthResult> checkEmailVerification(String idToken) {
        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("ID_TOKEN_REQUIRED", "Authentication token is required."));
        }
        return authDao.getAccountInfo(idToken);
    }

    public CompletableFuture<OperationResult<Void>> sendPasswordReset(String email) {
        OperationResult<Void> emailValidation = ValidationUtil.validateEmail(email);
        if (emailValidation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(emailValidation.getErrorCode(), emailValidation.getMessage()));
        }

        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        return authDao.sendPasswordReset(normalizedEmail);
    }

    public CompletableFuture<AuthResult> refreshToken(String refreshToken) {
        if (ValidationUtil.isBlank(refreshToken)) {
            return CompletableFuture.completedFuture(
                    AuthResult.failure("REFRESH_TOKEN_REQUIRED", "Refresh token is required."));
        }
        return authDao.refreshToken(refreshToken);
    }
}
