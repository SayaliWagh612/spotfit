package com.eklavya.spotfit.controller.user;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.service.FavoriteSpaceService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerFavoriteControllerTest {

    private FavoriteSpaceService mockService;
    private CustomerFavoriteController controller;
    private boolean isFavoriteData = false;

    @BeforeEach
    public void setUp() {
        AppSession.clearSession();
        mockService = new FavoriteSpaceService(null, null) {
            @Override
            public CompletableFuture<OperationResult<Boolean>> isFavorite(String userId, String spaceId,
                    String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(isFavoriteData));
            }

            @Override
            public CompletableFuture<OperationResult<Void>> addFavorite(String userId, String spaceId, String idToken) {
                isFavoriteData = true;
                return CompletableFuture.completedFuture(OperationResult.success(null));
            }

            @Override
            public CompletableFuture<OperationResult<Void>> removeFavorite(String userId, String spaceId,
                    String idToken) {
                isFavoriteData = false;
                return CompletableFuture.completedFuture(OperationResult.success(null));
            }
        };
        controller = new CustomerFavoriteController(mockService);
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testToggleFavorite_Unauthenticated() throws Exception {
        OperationResult<Boolean> res = controller.toggleFavorite("s1").get();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.AUTHENTICATION_REQUIRED, res.getErrorCode());
    }

    @Test
    public void testToggleFavorite_NotCustomer() throws Exception {
        AppSession.startSession("o1", "o@ex.com", "O", UserRole.OWNER, "tok", "ref", 3600);
        OperationResult<Boolean> res = controller.toggleFavorite("s1").get();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    public void testToggleFavorite_SuccessAdd() throws Exception {
        AppSession.startSession("u1", "u@ex.com", "U", UserRole.USER, "tok", "ref", 3600);
        isFavoriteData = false;
        OperationResult<Boolean> res = controller.toggleFavorite("s1").get();
        assertTrue(res.isSuccess());
        assertTrue(res.getData()); // New state is true
    }

    @Test
    public void testToggleFavorite_SuccessRemove() throws Exception {
        AppSession.startSession("u1", "u@ex.com", "U", UserRole.USER, "tok", "ref", 3600);
        isFavoriteData = true;
        OperationResult<Boolean> res = controller.toggleFavorite("s1").get();
        assertTrue(res.isSuccess());
        assertFalse(res.getData()); // New state is false
    }
}
