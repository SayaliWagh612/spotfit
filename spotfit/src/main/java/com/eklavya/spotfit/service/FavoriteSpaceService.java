package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.dao.FavoriteSpaceDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FavoriteSpaceService {

    private final FavoriteSpaceDao favoriteSpaceDao;
    private final SpaceService spaceService;

    public FavoriteSpaceService() {
        this.favoriteSpaceDao = new FavoriteSpaceDao();
        this.spaceService = new SpaceService();
    }

    public FavoriteSpaceService(FavoriteSpaceDao favoriteSpaceDao, SpaceService spaceService) {
        this.favoriteSpaceDao = favoriteSpaceDao;
        this.spaceService = spaceService;
    }

    public CompletableFuture<OperationResult<Boolean>> isFavorite(String userId, String spaceId, String idToken) {
        if (userId == null || userId.isBlank() || spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid arguments provided."));
        }
        return favoriteSpaceDao.isFavorite(userId, spaceId, idToken);
    }

    public CompletableFuture<OperationResult<Void>> addFavorite(String userId, String spaceId, String idToken) {
        if (userId == null || userId.isBlank() || spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid arguments provided."));
        }

        return spaceService.getSpace(spaceId, idToken).thenCompose(spaceRes -> {
            if (spaceRes.isFailure()) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        spaceRes.getErrorCode(), "Space is not accessible or does not exist."));
            }
            Space space = spaceRes.getData();
            if (space.getSchemaVersion() < 2 || !"CREATIVITY".equals(space.getCategory())) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        OperationCode.INVALID_ARGUMENT, "Only real V2 Creativity Spaces can be favorited."));
            }
            if (!"APPROVED".equals(space.getApprovalStatus()) || !"ACTIVE".equals(space.getOperationalStatus())) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        OperationCode.INVALID_ARGUMENT, "Space is not active or approved."));
            }

            long createdAt = System.currentTimeMillis();
            return favoriteSpaceDao.addFavorite(userId, spaceId, createdAt, idToken);
        });
    }

    public CompletableFuture<OperationResult<Void>> removeFavorite(String userId, String spaceId, String idToken) {
        if (userId == null || userId.isBlank() || spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid arguments provided."));
        }
        return favoriteSpaceDao.removeFavorite(userId, spaceId, idToken);
    }

    public CompletableFuture<OperationResult<List<Space>>> loadSavedVisibleSpaces(String userId, String idToken) {
        if (userId == null || userId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "User ID is required."));
        }

        return favoriteSpaceDao.getFavoriteSpaceIds(userId, idToken).thenCompose(idsRes -> {
            if (idsRes.isFailure()) {
                return CompletableFuture.completedFuture(OperationResult.failure(
                        idsRes.getErrorCode(), "Failed to load saved space IDs."));
            }

            List<String> spaceIds = idsRes.getData();
            if (spaceIds == null || spaceIds.isEmpty()) {
                return CompletableFuture.completedFuture(OperationResult.success(List.of()));
            }

            // Fetch current authoritative Space details asynchronously
            List<CompletableFuture<Space>> futures = spaceIds.stream()
                    .map(spaceId -> spaceService.getSpace(spaceId, idToken).thenApply(res -> {
                        if (res.isSuccess() && res.getData() != null) {
                            Space space = res.getData();
                            if (space.getSchemaVersion() >= 2
                                    && "APPROVED".equals(space.getApprovalStatus())
                                    && "ACTIVE".equals(space.getOperationalStatus())
                                    && "CREATIVITY".equals(space.getCategory())
                                    && space.isActive()) {
                                return space;
                            }
                        }
                        return null; // Exclude inaccessible or inactive
                    })).toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        List<Space> visibleSpaces = futures.stream()
                                .map(CompletableFuture::join)
                                .filter(space -> space != null)
                                .collect(Collectors.toList());
                        return OperationResult.success(visibleSpaces);
                    });
        });
    }
}
