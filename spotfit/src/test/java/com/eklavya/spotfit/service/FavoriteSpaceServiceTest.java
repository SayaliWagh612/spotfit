package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.dao.FavoriteSpaceDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class FavoriteSpaceServiceTest {

    private FavoriteSpaceDao mockDao;
    private SpaceService mockSpaceService;
    private FavoriteSpaceService service;

    @BeforeEach
    public void setUp() {
        mockDao = new FavoriteSpaceDao() {
            private List<String> favorites = new ArrayList<>();

            @Override
            public CompletableFuture<OperationResult<Void>> addFavorite(String userId, String spaceId, long createdAt,
                    String idToken) {
                if (!favorites.contains(spaceId))
                    favorites.add(spaceId);
                return CompletableFuture.completedFuture(OperationResult.success(null));
            }

            @Override
            public CompletableFuture<OperationResult<Void>> removeFavorite(String userId, String spaceId,
                    String idToken) {
                favorites.remove(spaceId);
                return CompletableFuture.completedFuture(OperationResult.success(null));
            }

            @Override
            public CompletableFuture<OperationResult<Boolean>> isFavorite(String userId, String spaceId,
                    String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(favorites.contains(spaceId)));
            }

            @Override
            public CompletableFuture<OperationResult<List<String>>> getFavoriteSpaceIds(String userId, String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>(favorites)));
            }
        };

        mockSpaceService = new SpaceService() {
            @Override
            public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
                if ("valid-space".equals(spaceId)) {
                    Space space = new Space();
                    space.setSpaceId("valid-space");
                    space.setActive(true);
                    space.setSchemaVersion(2);
                    space.setCategory("CREATIVITY");
                    space.setApprovalStatus("APPROVED");
                    space.setOperationalStatus("ACTIVE");
                    return CompletableFuture.completedFuture(OperationResult.success(space));
                } else if ("invalid-space".equals(spaceId)) {
                    Space space = new Space();
                    space.setSpaceId("invalid-space");
                    space.setActive(false); // Inactive
                    space.setSchemaVersion(2);
                    return CompletableFuture.completedFuture(OperationResult.success(space));
                }
                return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Not found"));
            }

            @Override
            public CompletableFuture<OperationResult<List<Space>>> getVisibleV2CreativitySpaces(
                    String activityGroup, String activityName, String idToken) {
                Space space = new Space();
                space.setSpaceId("valid-space");
                space.setActive(true);
                space.setSchemaVersion(2);
                space.setCategory("CREATIVITY");
                space.setApprovalStatus("APPROVED");
                space.setOperationalStatus("ACTIVE");
                return CompletableFuture.completedFuture(OperationResult.success(List.of(space)));
            }
        };

        service = new FavoriteSpaceService(mockDao, mockSpaceService);
    }

    @Test
    public void testAddFavorite_ValidSpace() throws Exception {
        OperationResult<Void> result = service.addFavorite("u1", "valid-space", "token").get();
        assertTrue(result.isSuccess());
        OperationResult<Boolean> check = service.isFavorite("u1", "valid-space", "token").get();
        assertTrue(check.getData());
    }

    @Test
    public void testAddFavorite_InvalidSpace() throws Exception {
        OperationResult<Void> result = service.addFavorite("u1", "invalid-space", "token").get();
        assertTrue(result.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, result.getErrorCode());
    }

    @Test
    public void testAddFavorite_NotFoundSpace() throws Exception {
        OperationResult<Void> result = service.addFavorite("u1", "missing-space", "token").get();
        assertTrue(result.isFailure());
        assertEquals(OperationCode.NOT_FOUND, result.getErrorCode());
    }

    @Test
    public void testLoadSavedVisibleSpaces() throws Exception {
        service.addFavorite("u1", "valid-space", "token").get();
        OperationResult<List<Space>> result = service.loadSavedVisibleSpaces("u1", "token").get();
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        assertEquals("valid-space", result.getData().get(0).getSpaceId());
    }
}
