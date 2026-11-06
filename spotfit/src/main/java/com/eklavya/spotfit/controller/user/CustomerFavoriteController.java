package com.eklavya.spotfit.controller.user;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.FavoriteSpaceService;
import com.eklavya.spotfit.service.AuthService;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CustomerFavoriteController {

    private final FavoriteSpaceService favoriteSpaceService;

    public CustomerFavoriteController() {
        this.favoriteSpaceService = new FavoriteSpaceService();
    }

    public CustomerFavoriteController(FavoriteSpaceService favoriteSpaceService) {
        this.favoriteSpaceService = favoriteSpaceService;
    }

    private CompletableFuture<OperationResult<Void>> validateCustomerSession() {
        if (!AppSession.isAuthenticated() || ValidationUtil.isBlank(AppSession.getUserId())
                || ValidationUtil.isBlank(AppSession.getIdToken())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User authentication is required."));
        }
        if (!"USER".equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.PERMISSION_DENIED,
                            "Only customers can perform this action."));
        }
        return CompletableFuture.completedFuture(OperationResult.success(null));
    }

    private <T> CompletableFuture<OperationResult<T>> executeWithTokenRefresh(
            Function<String, CompletableFuture<OperationResult<T>>> action) {
        CompletableFuture<String> tokenFuture;
        if (AppSession.shouldRefreshToken() && !ValidationUtil.isBlank(AppSession.getRefreshToken())) {
            AuthService authService = new AuthService();
            tokenFuture = authService.refreshToken(AppSession.getRefreshToken())
                    .thenApply(refreshResult -> {
                        if (refreshResult.isSuccess()) {
                            AppSession.updateTokens(
                                    refreshResult.getIdToken(),
                                    refreshResult.getRefreshToken(),
                                    refreshResult.getExpiresInSeconds());
                            return refreshResult.getIdToken();
                        }
                        return AppSession.getIdToken(); // Fallback
                    });
        } else {
            tokenFuture = CompletableFuture.completedFuture(AppSession.getIdToken());
        }
        return tokenFuture.thenCompose(action);
    }

    public CompletableFuture<OperationResult<Boolean>> isFavorite(String spaceId) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(OperationResult.success(false));
        }
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            return executeWithTokenRefresh(
                    (token) -> favoriteSpaceService.isFavorite(AppSession.getUserId(), spaceId, token));
        });
    }

    public CompletableFuture<OperationResult<Void>> addFavorite(String spaceId) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(OperationResult.failure(
                    OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            return executeWithTokenRefresh(
                    (token) -> favoriteSpaceService.addFavorite(AppSession.getUserId(), spaceId, token));
        });
    }

    public CompletableFuture<OperationResult<Void>> removeFavorite(String spaceId) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(OperationResult.failure(
                    OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            return executeWithTokenRefresh(
                    (token) -> favoriteSpaceService.removeFavorite(AppSession.getUserId(), spaceId, token));
        });
    }

    public CompletableFuture<OperationResult<Boolean>> toggleFavorite(String spaceId) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(OperationResult.failure(
                    OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        return isFavorite(spaceId).thenCompose(isFavRes -> {
            if (isFavRes.isFailure()) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        isFavRes.getErrorCode(), isFavRes.getMessage()));
            }
            boolean currentlySaved = isFavRes.getData();
            if (currentlySaved) {
                return removeFavorite(spaceId).thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(false); // New state is NOT favorite
                    }
                    return OperationResult.failure(res.getErrorCode(), res.getMessage());
                });
            } else {
                return addFavorite(spaceId).thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(true); // New state IS favorite
                    }
                    return OperationResult.failure(res.getErrorCode(), res.getMessage());
                });
            }
        });
    }

    public CompletableFuture<OperationResult<List<Space>>> loadSavedVisibleSpaces() {
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            return executeWithTokenRefresh(
                    (token) -> favoriteSpaceService.loadSavedVisibleSpaces(AppSession.getUserId(), token));
        });
    }
}
