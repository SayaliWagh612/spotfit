package com.eklavya.spotfit.controller.user;

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

import static org.junit.jupiter.api.Assertions.*;

public class CustomerSearchControllerTest {

    private SpaceService mockSpaceService;
    private CustomerMarketplaceController controller;

    @BeforeEach
    public void setUp() {
        AppSession.clearSession();
        mockSpaceService = new SpaceService() {
            @Override
            public CompletableFuture<OperationResult<List<Space>>> getVisibleV2CreativitySpaces(String activityGroup,
                    String activityName, String idToken) {
                if ("Art".equalsIgnoreCase(activityGroup)) {
                    Space s1 = new Space();
                    s1.setSpaceId("s1");
                    s1.setSpaceName("Art Studio Alpha");
                    s1.setActivityGroup("Art");
                    s1.setActivityType("Painting");

                    Space s2 = new Space();
                    s2.setSpaceId("s2");
                    s2.setSpaceName("Alpha Music Hub"); // Art group but has Alpha
                    s2.setActivityGroup("Art");
                    s2.setActivityType("Mixed");

                    return CompletableFuture.completedFuture(OperationResult.success(List.of(s1, s2)));
                } else if ("Music".equalsIgnoreCase(activityGroup)) {
                    Space s3 = new Space();
                    s3.setSpaceId("s3");
                    s3.setSpaceName("Rhythm Beta");
                    s3.setActivityGroup("Music");
                    s3.setActivityType("Recording");
                    return CompletableFuture.completedFuture(OperationResult.success(List.of(s3)));
                }
                return CompletableFuture.completedFuture(OperationResult.success(List.of()));
            }

            @Override
            public CompletableFuture<OperationResult<List<Space>>> getPublicVisibleV2CreativitySpaces(
                    String activityGroup, String activityName) {
                return getVisibleV2CreativitySpaces(activityGroup, activityName, null);
            }
        };
        controller = new CustomerMarketplaceController(mockSpaceService);
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testSearchVisibleMarketplaceSpaces_Unauthenticated() throws Exception {
        OperationResult<List<Space>> result = controller.searchVisibleMarketplaceSpaces("Alpha").get();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
    }

    @Test
    public void testSearchVisibleMarketplaceSpaces_BlankQuery() throws Exception {
        AppSession.startSession("user-1", "user@example.com", "Test User", UserRole.USER, "token-1", "refresh-1", 3600);
        OperationResult<List<Space>> result = controller.searchVisibleMarketplaceSpaces(" ").get();
        assertTrue(result.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, result.getErrorCode());
    }

    @Test
    public void testSearchVisibleMarketplaceSpaces_SuccessAndRanking() throws Exception {
        AppSession.startSession("user-1", "user@example.com", "Test User", UserRole.USER, "token-1", "refresh-1", 3600);
        OperationResult<List<Space>> result = controller.searchVisibleMarketplaceSpaces("alpha").get();
        assertTrue(result.isSuccess());

        List<Space> matches = result.getData();
        assertEquals(2, matches.size());

        // "Alpha Music Hub" starts with "alpha", "Art Studio Alpha" contains "alpha"
        // so "Alpha Music Hub" should be ranked first.
        assertEquals("Alpha Music Hub", matches.get(0).getSpaceName());
        assertEquals("Art Studio Alpha", matches.get(1).getSpaceName());
    }

    @Test
    public void testSearchVisibleMarketplaceSpaces_NoMatches() throws Exception {
        AppSession.startSession("user-1", "user@example.com", "Test User", UserRole.USER, "token-1", "refresh-1", 3600);
        OperationResult<List<Space>> result = controller.searchVisibleMarketplaceSpaces("NonExistent").get();
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testSearchVisibleMarketplaceSpaces_ActivityMatch() throws Exception {
        AppSession.startSession("user-1", "user@example.com", "Test User", UserRole.USER, "token-1", "refresh-1", 3600);
        OperationResult<List<Space>> result = controller.searchVisibleMarketplaceSpaces("Painting").get();
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        assertEquals("Art Studio Alpha", result.getData().get(0).getSpaceName());
    }
}
