package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.dao.InvitationDao;
import com.eklavya.spotfit.dao.ManagerDao;
import com.eklavya.spotfit.dao.OwnerDao;
import com.eklavya.spotfit.dao.UserDao;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.service.AuthService;
import com.eklavya.spotfit.service.InvitationService;
import com.eklavya.spotfit.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerAuthControllerTest {

    private AuthService mockAuthService;
    private InvitationService mockInvitationService;
    private ManagerService mockManagerService;
    private UserDao mockUserDao;
    private OwnerDao mockOwnerDao;
    private ManagerDao mockManagerDao;
    private InvitationDao mockInvitationDao;

    private ManagerAuthController controller;
    private ManagerInvitation validInvitation;

    @BeforeEach
    public void setUp() {
        validInvitation = new ManagerInvitation("owner-123", "manager@example.com", List.of("space-1"), List.of("MANAGE_BOOKINGS"));
        validInvitation.setInvitationCode("CODE123456789012");

        mockAuthService = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> signUp(String email, String password) {
                if ("existing@example.com".equalsIgnoreCase(email)) {
                    return CompletableFuture.completedFuture(
                            AuthResult.failure(OperationCode.EMAIL_ALREADY_EXISTS, "EMAIL_EXISTS: Account already exists.")
                    );
                }
                AuthResult authRes = AuthResult.success("uid-999", email, "token-999", "refresh-999", 3600, false, "Signup successful.");
                return CompletableFuture.completedFuture(authRes);
            }

            @Override
            public CompletableFuture<AuthResult> signIn(String email, String password) {
                if ("wrongpass@example.com".equalsIgnoreCase(email)) {
                    return CompletableFuture.completedFuture(AuthResult.failure("INVALID_PASSWORD", "Invalid credentials."));
                }
                boolean verified = !"unverified@example.com".equalsIgnoreCase(email);
                AuthResult authRes = AuthResult.success("uid-999", email, "token-999", "refresh-999", 3600, verified, "Sign in successful.");
                return CompletableFuture.completedFuture(authRes);
            }

            @Override
            public CompletableFuture<OperationResult<Void>> sendEmailVerification(String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(null));
            }

            @Override
            public CompletableFuture<AuthResult> refreshToken(String refreshToken) {
                AuthResult refreshed = AuthResult.success("uid-999", "manager@example.com", "fresh-token-999", refreshToken, 3600, true, "Refreshed.");
                return CompletableFuture.completedFuture(refreshed);
            }

            @Override
            public CompletableFuture<AuthResult> checkEmailVerification(String idToken) {
                AuthResult info = AuthResult.success("uid-999", "manager@example.com", idToken, "refresh-999", 3600, true, "Verified.");
                return CompletableFuture.completedFuture(info);
            }
        };

        mockInvitationDao = new InvitationDao() {
            @Override
            public CompletableFuture<OperationResult<ManagerInvitation>> getInvitationForRegistration(String code) {
                return getInvitationByCode(code, null);
            }

            @Override
            public CompletableFuture<OperationResult<ManagerInvitation>> getInvitationByCode(String code, String idToken) {
                if ("INVALIDCODE".equalsIgnoreCase(code)) {
                    return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Invitation not found."));
                }
                if ("EXPIREDCODE12345".equalsIgnoreCase(code)) {
                    ManagerInvitation exp = new ManagerInvitation("owner-123", "manager@example.com", List.of("space-1"), List.of("MANAGE_BOOKINGS"));
                    exp.setInvitationCode(code);
                    exp.setExpiresAt(System.currentTimeMillis() - 100000);
                    return CompletableFuture.completedFuture(OperationResult.success(exp));
                }
                if ("ZEROSPACE123456".equalsIgnoreCase(code)) {
                    ManagerInvitation zeroSpace = new ManagerInvitation("owner-123", "manager@example.com", List.of(), List.of("MANAGE_BOOKINGS"));
                    zeroSpace.setInvitationCode(code);
                    return CompletableFuture.completedFuture(OperationResult.success(zeroSpace));
                }
                return CompletableFuture.completedFuture(OperationResult.success(validInvitation));
            }
        };

        mockInvitationService = new InvitationService(mockInvitationDao);

        mockUserDao = new UserDao() {
            @Override
            public CompletableFuture<OperationResult<User>> getUser(String userId, String idToken) {
                if ("customer-uid".equalsIgnoreCase(userId)) {
                    User customer = User.createNewUser(userId, "Customer User", "customer@example.com", "+919999999999");
                    return CompletableFuture.completedFuture(OperationResult.success(customer));
                }
                return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "User profile not found."));
            }
        };

        mockOwnerDao = new OwnerDao() {
            @Override
            public CompletableFuture<OperationResult<Owner>> getOwner(String ownerId, String idToken) {
                if ("owner-uid".equalsIgnoreCase(ownerId)) {
                    Owner owner = new Owner("owner-uid", "Owner User", "owner@example.com", "+919999999999", "OWNER", "", "City", true, true, true, 0, 0);
                    return CompletableFuture.completedFuture(OperationResult.success(owner));
                }
                return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Owner profile not found."));
            }
        };

        mockManagerDao = new ManagerDao() {
            @Override
            public CompletableFuture<OperationResult<Manager>> getManager(String managerId, String idToken) {
                if ("diff-owner-mgr-uid".equalsIgnoreCase(managerId)) {
                    Manager mgr = new Manager("diff-owner-mgr-uid", "other-owner-456", "Mgr Name", "mgr@example.com", "+919999999999", List.of("sp-1"), List.of("MANAGE_BOOKINGS"), true, true, true, 0, 0);
                    return CompletableFuture.completedFuture(OperationResult.success(mgr));
                }
                return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Manager profile not found."));
            }
        };

        mockManagerService = new ManagerService(mockManagerDao, mockInvitationDao) {
            @Override
            public CompletableFuture<OperationResult<Manager>> activateManagerAccountAtomically(
                    String managerId, String name, String email, String phone,
                    boolean emailVerified, String invitationCode, String idToken) {
                Manager mgr = new Manager(managerId, "owner-123", name, email, phone, List.of("space-1"), List.of("MANAGE_BOOKINGS"), emailVerified, true, true, System.currentTimeMillis(), System.currentTimeMillis());
                return CompletableFuture.completedFuture(OperationResult.success(mgr, "Manager profile created atomically."));
            }
        };

        controller = new ManagerAuthController(mockAuthService, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);
    }

    @Test
    public void testRegisterManager_InvalidInvitationCode_RejectsWithoutAuthSignup() throws ExecutionException, InterruptedException {
        OperationResult<AuthResult> result = controller.registerManager(
                "INVALIDCODE", "Test Name", "manager@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("INVITATION_NOT_FOUND", result.getErrorCode());
    }

    @Test
    public void testRegisterManager_ExpiredInvitation_RejectsWithoutAuthSignup() throws ExecutionException, InterruptedException {
        OperationResult<AuthResult> result = controller.registerManager(
                "EXPIREDCODE12345", "Test Name", "manager@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("INVITATION_EXPIRED", result.getErrorCode());
    }

    @Test
    public void testRegisterManager_EmailMismatch_RejectsWithoutAuthSignup() throws ExecutionException, InterruptedException {
        OperationResult<AuthResult> result = controller.registerManager(
                "CODE123456789012", "Test Name", "wrongemail@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("EMAIL_MISMATCH", result.getErrorCode());
    }

    @Test
    public void testRegisterManager_ZeroAssignedSpaces_RejectsWithoutAuthSignup() throws ExecutionException, InterruptedException {
        OperationResult<AuthResult> result = controller.registerManager(
                "ZEROSPACE123456", "Test Name", "manager@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("ZERO_SPACES", result.getErrorCode());
    }

    @Test
    public void testRegisterManager_EmailExists_ReturnsEmailAlreadyExistsCode() throws ExecutionException, InterruptedException {
        ManagerInvitation expInv = new ManagerInvitation("owner-123", "existing@example.com", List.of("space-1"), List.of("MANAGE_BOOKINGS"));
        expInv.setInvitationCode("EXISTINGCODE1234");
        mockInvitationDao = new InvitationDao() {
            @Override
            public CompletableFuture<OperationResult<ManagerInvitation>> getInvitationForRegistration(String code) {
                return CompletableFuture.completedFuture(OperationResult.success(expInv));
            }
            @Override
            public CompletableFuture<OperationResult<ManagerInvitation>> getInvitationByCode(String code, String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(expInv));
            }
        };

        InvitationService invSvc = new InvitationService(mockInvitationDao);
        ManagerAuthController testController = new ManagerAuthController(mockAuthService, mockManagerService, invSvc, mockUserDao, mockOwnerDao);

        OperationResult<AuthResult> result = testController.registerManager(
                "EXISTINGCODE1234", "Existing Name", "existing@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(result.isFailure());
        assertEquals(OperationCode.EMAIL_ALREADY_EXISTS, result.getErrorCode());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    public void testRegisterManager_Success_SendsEmailVerification() throws ExecutionException, InterruptedException {
        OperationResult<AuthResult> result = controller.registerManager(
                "CODE123456789012", "Manager Name", "manager@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getMessage().contains("Verification email sent"));
    }

    @Test
    public void testLoginAndContinueInvitation_CustomerAccount_RejectsWithCustomerError() throws ExecutionException, InterruptedException {
        AuthService customAuth = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> signIn(String email, String password) {
                return CompletableFuture.completedFuture(AuthResult.success("customer-uid", email, "tok", "ref", 3600, true, "OK"));
            }
        };
        ManagerAuthController testCtrl = new ManagerAuthController(customAuth, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);

        OperationResult<Manager> result = testCtrl.loginManagerAndContinueInvitation(
                "manager@example.com", "Pass123!", "CODE123456789012"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("REJECTED_CUSTOMER_ACCOUNT", result.getErrorCode());
    }

    @Test
    public void testLoginAndContinueInvitation_OwnerAccount_RejectsWithOwnerError() throws ExecutionException, InterruptedException {
        AuthService customAuth = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> signIn(String email, String password) {
                return CompletableFuture.completedFuture(AuthResult.success("owner-uid", email, "tok", "ref", 3600, true, "OK"));
            }
        };
        ManagerAuthController testCtrl = new ManagerAuthController(customAuth, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);

        OperationResult<Manager> result = testCtrl.loginManagerAndContinueInvitation(
                "manager@example.com", "Pass123!", "CODE123456789012"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("REJECTED_OWNER_ACCOUNT", result.getErrorCode());
    }

    @Test
    public void testLoginAndContinueInvitation_ExistingManagerDifferentOwner_RejectsWithMultipleOwnersError() throws ExecutionException, InterruptedException {
        AuthService customAuth = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> signIn(String email, String password) {
                return CompletableFuture.completedFuture(AuthResult.success("diff-owner-mgr-uid", email, "tok", "ref", 3600, true, "OK"));
            }
        };
        ManagerAuthController testCtrl = new ManagerAuthController(customAuth, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);

        OperationResult<Manager> result = testCtrl.loginManagerAndContinueInvitation(
                "manager@example.com", "Pass123!", "CODE123456789012"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("MULTIPLE_OWNERS_UNSUPPORTED", result.getErrorCode());
    }

    @Test
    public void testLoginAndContinueInvitation_UnverifiedEmail_ReturnsEmailNotVerifiedError() throws ExecutionException, InterruptedException {
        AuthService customAuth = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> signIn(String email, String password) {
                return CompletableFuture.completedFuture(AuthResult.success("orphan-uid", email, "tok", "ref", 3600, false, "Unverified"));
            }
        };
        ManagerAuthController testCtrl = new ManagerAuthController(customAuth, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);

        OperationResult<Manager> result = testCtrl.loginManagerAndContinueInvitation(
                "manager@example.com", "Pass123!", "CODE123456789012"
        ).get();

        assertTrue(result.isFailure());
        assertEquals("EMAIL_NOT_VERIFIED", result.getErrorCode());
    }

    @Test
    public void testLoginAndContinueInvitation_VerifiedOrphanAuthAccount_AtomicallyCreatesManagerAndAcceptsInvitation() throws ExecutionException, InterruptedException {
        AuthService customAuth = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> signIn(String email, String password) {
                return CompletableFuture.completedFuture(AuthResult.success("orphan-uid", email, "tok", "ref", 3600, true, "OK"));
            }
        };
        ManagerAuthController testCtrl = new ManagerAuthController(customAuth, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);

        OperationResult<Manager> result = testCtrl.loginManagerAndContinueInvitation(
                "manager@example.com", "Pass123!", "CODE123456789012"
        ).get();

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("orphan-uid", result.getData().getManagerId());
    }

    @Test
    public void testFullRecoveryLifecycle_NewSignup_AppReopen_SignIn_Verify_Activate() throws ExecutionException, InterruptedException {
        // 1. Initial signup produces Auth user but email is unverified
        OperationResult<AuthResult> signupRes = controller.registerManager(
                "CODE123456789012", "Manager Name", "manager@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(signupRes.isSuccess());
        assertFalse(signupRes.getData().isEmailVerified());

        // 2. App reopens, user re-submits registration with same email -> EMAIL_ALREADY_EXISTS
        AuthService existingAuth = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> signUp(String email, String password) {
                return CompletableFuture.completedFuture(
                        AuthResult.failure(OperationCode.EMAIL_ALREADY_EXISTS, "EMAIL_EXISTS: Account already exists.")
                );
            }
            @Override
            public CompletableFuture<AuthResult> signIn(String email, String password) {
                return CompletableFuture.completedFuture(
                        AuthResult.success("uid-999", email, "tok-999", "ref-999", 3600, true, "OK")
                );
            }
        };

        ManagerAuthController reopensCtrl = new ManagerAuthController(existingAuth, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);

        OperationResult<AuthResult> retrySignup = reopensCtrl.registerManager(
                "CODE123456789012", "Manager Name", "manager@example.com", "+919999999999", "Pass123!", "Pass123!"
        ).get();

        assertTrue(retrySignup.isFailure());
        assertEquals(OperationCode.EMAIL_ALREADY_EXISTS, retrySignup.getErrorCode());

        // 3. User selects "Sign In & Continue Invitation" -> recovers, verifies email, atomically activates profile
        OperationResult<Manager> recoveryResult = reopensCtrl.loginManagerAndContinueInvitation(
                "manager@example.com", "Pass123!", "CODE123456789012"
        ).get();

        assertTrue(recoveryResult.isSuccess());
        assertNotNull(recoveryResult.getData());
        assertEquals("uid-999", recoveryResult.getData().getManagerId());
    }

    @Test
    public void testCheckVerificationAndActivateManager_MissingAuthResult_ReturnsMissingRecoveryContext() throws ExecutionException, InterruptedException {
        OperationResult<Manager> res = controller.checkVerificationAndActivateManager(
                null, "CODE123456789012", "Manager Name", "+919999999999"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("MISSING_RECOVERY_CONTEXT", res.getErrorCode());
    }

    @Test
    public void testCheckVerificationAndActivateManager_UnverifiedEmail_ReturnsEmailNotVerified() throws ExecutionException, InterruptedException {
        AuthService unverifiedAuthSvc = new AuthService() {
            @Override
            public CompletableFuture<AuthResult> refreshToken(String refreshToken) {
                return CompletableFuture.completedFuture(AuthResult.success("uid-999", "manager@example.com", "fresh-token-999", refreshToken, 3600, false, "Refreshed."));
            }
            @Override
            public CompletableFuture<AuthResult> checkEmailVerification(String idToken) {
                return CompletableFuture.completedFuture(AuthResult.success("uid-999", "manager@example.com", idToken, "refresh-999", 3600, false, "Unverified."));
            }
        };

        ManagerAuthController testCtrl = new ManagerAuthController(unverifiedAuthSvc, mockManagerService, mockInvitationService, mockUserDao, mockOwnerDao);
        AuthResult authRes = AuthResult.success("uid-999", "manager@example.com", "tok-999", "ref-999", 3600, false, "OK");

        OperationResult<Manager> res = testCtrl.checkVerificationAndActivateManager(
                authRes, "CODE123456789012", "Manager Name", "+919999999999"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("EMAIL_NOT_VERIFIED", res.getErrorCode());
    }

    @Test
    public void testCheckVerificationAndActivateManager_VerifiedEmail_RefreshesTokenAndActivates() throws ExecutionException, InterruptedException {
        AuthResult authRes = AuthResult.success("uid-999", "manager@example.com", "tok-999", "ref-999", 3600, false, "OK");

        OperationResult<Manager> res = controller.checkVerificationAndActivateManager(
                authRes, "CODE123456789012", "Manager Name", "+919999999999"
        ).get();

        assertTrue(res.isSuccess());
        assertNotNull(res.getData());
        assertEquals("uid-999", res.getData().getManagerId());
    }

    @Test
    public void testFirestoreDocumentParsing_WithFields_ParsesSuccessfully() {
        org.json.JSONObject doc = new org.json.JSONObject()
                .put("fields", new org.json.JSONObject()
                        .put("name", new org.json.JSONObject().put("stringValue", "Test User")));

        OperationResult<org.json.JSONObject> result = com.eklavya.spotfit.util.FirestoreDocumentUtil.parseFirestoreDocument(doc);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().has("name"));
    }

    @Test
    public void testFirestoreDocumentParsing_NoFields_ReturnsInvalidResponse() {
        org.json.JSONObject authOrCommitResponse = new org.json.JSONObject()
                .put("writeResults", new org.json.JSONArray())
                .put("commitTime", "2026-09-03T12:00:00Z");

        OperationResult<org.json.JSONObject> result = com.eklavya.spotfit.util.FirestoreDocumentUtil.parseFirestoreDocument(authOrCommitResponse);

        assertTrue(result.isFailure());
        assertEquals(OperationCode.INVALID_RESPONSE, result.getErrorCode());
    }

    @Test
    public void testFirestoreDocumentParsing_MissingFields_NoJSONException() {
        org.json.JSONObject emptyJson = new org.json.JSONObject();
        assertDoesNotThrow(() -> {
            OperationResult<org.json.JSONObject> result = com.eklavya.spotfit.util.FirestoreDocumentUtil.parseFirestoreDocument(emptyJson);
            assertTrue(result.isFailure());
            assertEquals(OperationCode.INVALID_RESPONSE, result.getErrorCode());
        });
    }

    @Test
    public void testCommitResponse_NotParsedAsDocument() throws ExecutionException, InterruptedException {
        AuthResult authRes = AuthResult.success("uid-999", "manager@example.com", "tok-999", "ref-999", 3600, false, "OK");

        OperationResult<Manager> res = controller.checkVerificationAndActivateManager(
                authRes, "CODE123456789012", "Manager Name", "+919999999999"
        ).get();

        assertTrue(res.isSuccess());
        assertNotNull(res.getData());
        assertEquals("uid-999", res.getData().getManagerId());
        assertEquals("owner-123", res.getData().getOwnerId());
    }

    @Test
    public void testFailedCommit_ReturnsOriginalErrorCode() throws ExecutionException, InterruptedException {
        ManagerService failingManagerService = new ManagerService(mockManagerDao, mockInvitationDao) {
            @Override
            public CompletableFuture<OperationResult<Manager>> activateManagerAccountAtomically(
                    String managerId, String name, String email, String phone,
                    boolean emailVerified, String invitationCode, String idToken) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied during atomic commit.")
                );
            }
        };

        ManagerAuthController failCtrl = new ManagerAuthController(mockAuthService, failingManagerService, mockInvitationService, mockUserDao, mockOwnerDao);
        AuthResult authRes = AuthResult.success("uid-999", "manager@example.com", "tok-999", "ref-999", 3600, false, "OK");

        OperationResult<Manager> res = failCtrl.checkVerificationAndActivateManager(
                authRes, "CODE123456789012", "Manager Name", "+919999999999"
        ).get();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }
}
