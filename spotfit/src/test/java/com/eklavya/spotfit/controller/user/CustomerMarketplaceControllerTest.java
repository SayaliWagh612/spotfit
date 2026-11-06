package com.eklavya.spotfit.controller.user;

import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerMarketplaceControllerTest {

    private SpaceService mockSpaceService;
    private CustomerMarketplaceController controller;

    @BeforeEach
    public void setUp() {
        AppSession.clearSession();
        mockSpaceService = new SpaceService() {
            @Override
            public CompletableFuture<OperationResult<List<Space>>> getLiveCreativitySpaces(String activityGroup,
                    String idToken) {
                if ("ART".equalsIgnoreCase(activityGroup)) {
                    Space s = new Space();
                    s.setSpaceId("s1");
                    s.setSpaceName("Art Studio");
                    return CompletableFuture.completedFuture(OperationResult.success(List.of(s)));
                }
                return CompletableFuture.completedFuture(OperationResult.success(List.of()));
            }

            @Override
            public CompletableFuture<OperationResult<List<Space>>> getPublicLiveCreativitySpaces(String activityGroup) {
                if ("ART".equalsIgnoreCase(activityGroup)) {
                    Space s = new Space();
                    s.setSpaceId("s1");
                    s.setSpaceName("Public Art Studio");
                    return CompletableFuture.completedFuture(OperationResult.success(List.of(s)));
                }
                return CompletableFuture.completedFuture(OperationResult.success(List.of()));
            }

            @Override
            public CompletableFuture<OperationResult<List<Space>>> getPublicVisibleV2CreativitySpaces(
                    String activityGroup, String activityName) {
                if ("ART".equalsIgnoreCase(activityGroup)) {
                    Space s = new Space();
                    s.setSpaceId("s1");
                    s.setSpaceName("Public Art Studio");
                    return CompletableFuture.completedFuture(OperationResult.success(List.of(s)));
                }
                return CompletableFuture.completedFuture(OperationResult.success(List.of()));
            }
        };
        controller = new CustomerMarketplaceController(mockSpaceService);
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testLoadLiveCreativitySpaces_Unauthenticated() throws Exception {
        OperationResult<List<Space>> result = controller.loadLiveCreativitySpaces("Art").get();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("Public Art Studio", result.getData().get(0).getSpaceName());
    }

    @Test
    public void testLoadLiveCreativitySpaces_RoleMismatch() throws Exception {
        AppSession.startSession("user-1", "user@example.com", "Test User", UserRole.OWNER, "token-1", "refresh-1",
                3600);
        OperationResult<List<Space>> result = controller.loadLiveCreativitySpaces("Art").get();
        assertTrue(result.isFailure());
        assertEquals(OperationCode.ROLE_MISMATCH, result.getErrorCode());
    }

    @Test
    public void testLoadLiveCreativitySpaces_InvalidGroup() throws Exception {
        AppSession.startSession("user-1", "user@example.com", "Test User", UserRole.USER, "token-1", "refresh-1", 3600);
        OperationResult<List<Space>> result = controller.loadLiveCreativitySpaces("InvalidGroup").get();
        assertTrue(result.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, result.getErrorCode());
    }

    @Test
    public void testLoadLiveCreativitySpaces_Success() throws Exception {
        AppSession.startSession("user-1", "user@example.com", "Test User", UserRole.USER, "token-1", "refresh-1", 3600);
        OperationResult<List<Space>> result = controller.loadLiveCreativitySpaces("Art").get();
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        assertEquals("Art Studio", result.getData().get(0).getSpaceName());
    }

    @Test
    public void testMapNavigationGroupToActivityGroup() {
        assertEquals(CreativityActivityGroup.ART, controller.mapNavigationGroupToActivityGroup("Art"));
        assertEquals(CreativityActivityGroup.CONTENT_CREATION,
                controller.mapNavigationGroupToActivityGroup("Content Creation"));
        assertEquals(CreativityActivityGroup.PODCASTS, controller.mapNavigationGroupToActivityGroup("Podcasts"));
        assertEquals(CreativityActivityGroup.MUSIC, controller.mapNavigationGroupToActivityGroup("Music"));
        assertEquals(CreativityActivityGroup.ART, controller.mapNavigationGroupToActivityGroup("ART"));
        assertNull(controller.mapNavigationGroupToActivityGroup("Unknown"));
        assertNull(controller.mapNavigationGroupToActivityGroup(null));
    }
}
