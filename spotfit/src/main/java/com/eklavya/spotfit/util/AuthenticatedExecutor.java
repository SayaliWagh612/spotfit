package com.eklavya.spotfit.util;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.service.AuthService;
import com.eklavya.spotfit.session.AppSession;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Reusable execution wrapper for authenticated backend operations.
 * If an operation fails with HTTP 401 / AUTHENTICATION_REQUIRED, it uses single-flight
 * refresh to obtain a new ID token, updates AppSession atomically, and retries the operation exactly once.
 */
public class AuthenticatedExecutor {

    private static AuthService authService = new AuthService();
    private static CompletableFuture<AuthResult> inFlightRefresh = null;
    private static final Object lock = new Object();

    public static void setAuthServiceForTesting(AuthService testAuthService) {
        authService = (testAuthService != null) ? testAuthService : new AuthService();
    }

    public static <T> CompletableFuture<OperationResult<T>> execute(
            Function<String, CompletableFuture<OperationResult<T>>> operationSupplier) {

        if (!AppSession.isAuthenticated() || ValidationUtil.isBlank(AppSession.getIdToken())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User authentication is required."));
        }

        String initialToken = AppSession.getIdToken();
        String initialUserId = AppSession.getUserId();

        return operationSupplier.apply(initialToken).thenCompose(result -> {
            if (result != null && result.isFailure() && OperationCode.AUTHENTICATION_REQUIRED.equals(result.getErrorCode())) {
                // Initial request failed with HTTP 401 / AUTHENTICATION_REQUIRED. Attempt single-flight token refresh.
                return refreshTokenSingleFlight().thenCompose(refreshResult -> {
                    if (refreshResult != null && refreshResult.isSuccess() && refreshResult.getIdToken() != null) {
                        // Verify session UID has not changed during refresh
                        if (initialUserId == null || !initialUserId.equals(AppSession.getUserId())) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Session changed during authentication renewal."));
                        }
                        // Retry operation exactly once with the renewed token
                        return operationSupplier.apply(AppSession.getIdToken());
                    } else {
                        // Refresh failed or token invalid -> clear affected session safely
                        AppSession.clearSession();
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication token expired. Please sign in again."));
                    }
                });
            }
            return CompletableFuture.completedFuture(result);
        });
    }

    public static CompletableFuture<AuthResult> refreshTokenSingleFlight() {
        synchronized (lock) {
            if (inFlightRefresh != null && !inFlightRefresh.isDone()) {
                return inFlightRefresh;
            }

            String currentRefreshToken = AppSession.getRefreshToken();
            if (ValidationUtil.isBlank(currentRefreshToken)) {
                return CompletableFuture.completedFuture(
                        AuthResult.failure("REFRESH_TOKEN_MISSING", "No refresh token available."));
            }

            CompletableFuture<AuthResult> refreshFuture = authService.refreshToken(currentRefreshToken).thenApply(authResult -> {
                if (authResult != null && authResult.isSuccess() && authResult.getIdToken() != null) {
                    AppSession.updateTokens(authResult.getIdToken(), authResult.getRefreshToken(), authResult.getExpiresInSeconds());
                }
                return authResult;
            });

            refreshFuture.whenComplete((res, ex) -> {
                synchronized (lock) {
                    inFlightRefresh = null;
                }
            });

            inFlightRefresh = refreshFuture;
            return refreshFuture;
        }
    }
}
