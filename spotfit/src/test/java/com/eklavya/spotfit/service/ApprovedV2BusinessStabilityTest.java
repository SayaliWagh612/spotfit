package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test suite verifying Approved V2 Business operations stability,
 * space query scoping, eligibility predicate enforcement, and Firestore deserialization resilience.
 */
public class ApprovedV2BusinessStabilityTest {

    private BusinessService businessService;
    private SpaceService spaceService;

    @BeforeEach
    public void setUp() {
        businessService = new BusinessService();
        spaceService = new SpaceService();
    }

    @Test
    @DisplayName("YoYo Approved V2 Creativity Business must be strictly eligible for space creation")
    public void testApprovedV2CreativityBusinessEligibility() {
        String ownerId = "anushka_tupe_uid_123";

        Business yoyo = Business.createV2(
                "yoyo_biz_id_999",
                ownerId,
                "YoYo Studio",
                "YoYo Pvt Ltd",
                "yoyo@example.com",
                "+919876543210",
                "Creative Recording Studio",
                "123 Music Lane",
                "Near Metro",
                "Pune",
                "Maharashtra",
                "411001",
                "REG12345",
                SpaceCategory.CREATIVITY,
                Arrays.asList("MUSIC", "DANCE"),
                "",
                "https://yoyo.com",
                BusinessStatus.APPROVED,
                true,
                System.currentTimeMillis() - 86400000L,
                System.currentTimeMillis() - 3600000L,
                "",
                System.currentTimeMillis() - 86400000L,
                System.currentTimeMillis()
        );

        assertTrue(businessService.isEligibleForV2SpaceCreation(yoyo, ownerId),
                "YoYo approved V2 business must evaluate to true for space creation eligibility.");

        List<String> failures = businessService.getV2SpaceEligibilityFailures(yoyo, ownerId);
        assertTrue(failures.isEmpty(), "YoYo business should have zero eligibility failure reasons.");
    }

    @Test
    @DisplayName("Ineligible businesses must return precise failure reasons")
    public void testIneligibleBusinessFailureReasons() {
        String ownerId = "owner_456";

        // DRAFT Business
        Business draftBiz = Business.createV2(
                "b1", ownerId, "Draft Biz", "Draft Legal", "d@ex.com", "+919876543210",
                "desc", "add", "land", "City", "State", "411001", "REG",
                SpaceCategory.CREATIVITY, Arrays.asList("MUSIC"), "", "",
                BusinessStatus.DRAFT, true, 0L, 0L, "", 100L, 100L
        );

        assertFalse(businessService.isEligibleForV2SpaceCreation(draftBiz, ownerId));
        List<String> draftFailures = businessService.getV2SpaceEligibilityFailures(draftBiz, ownerId);
        assertFalse(draftFailures.isEmpty());
        assertTrue(draftFailures.stream().anyMatch(f -> f.contains("DRAFT") || f.contains("APPROVED required")));

        // Wrong Owner
        assertFalse(businessService.isEligibleForV2SpaceCreation(draftBiz, "different_owner_uid"));
        List<String> ownerFailures = businessService.getV2SpaceEligibilityFailures(draftBiz, "different_owner_uid");
        assertTrue(ownerFailures.stream().anyMatch(f -> f.contains("owner mismatch")));
    }

    @Test
    @DisplayName("SpaceDao getSpacesByOwnerAndBusiness requires ownerId and businessId parameters")
    public void testSpaceDaoGetSpacesByOwnerAndBusinessValidation() {
        SpaceDao spaceDao = new SpaceDao();

        CompletableFuture<OperationResult<List<Space>>> resNoOwner = spaceDao.getSpacesByOwnerAndBusiness("", "biz_1", "token_1");
        assertTrue(resNoOwner.join().isFailure());
        assertEquals("Owner ID is required.", resNoOwner.join().getMessage());

        CompletableFuture<OperationResult<List<Space>>> resNoBiz = spaceDao.getSpacesByOwnerAndBusiness("owner_1", "", "token_1");
        assertTrue(resNoBiz.join().isFailure());
        assertEquals("Business ID is required.", resNoBiz.join().getMessage());

        CompletableFuture<OperationResult<List<Space>>> resNoToken = spaceDao.getSpacesByOwnerAndBusiness("owner_1", "biz_1", "");
        assertTrue(resNoToken.join().isFailure());
        assertEquals("ID token is required.", resNoToken.join().getMessage());
    }

    @Test
    @DisplayName("FirestoreDocumentUtil deserializes boolean and integer typed representations flexibly")
    public void testFirestoreDocumentUtilFlexibility() {
        // StringValue true
        JSONObject strTrueFields = new JSONObject();
        strTrueFields.put("active", new JSONObject().put("stringValue", "true"));
        assertTrue(FirestoreDocumentUtil.getBoolean(strTrueFields, "active", false));

        // BooleanValue true
        JSONObject boolTrueFields = new JSONObject();
        boolTrueFields.put("active", new JSONObject().put("booleanValue", true));
        assertTrue(FirestoreDocumentUtil.getBoolean(boolTrueFields, "active", false));

        // StringValue integer schemaVersion "2"
        JSONObject strSchemaFields = new JSONObject();
        strSchemaFields.put("schemaVersion", new JSONObject().put("stringValue", "2"));
        assertEquals(2, FirestoreDocumentUtil.getInt(strSchemaFields, "schemaVersion", 1));

        // IntegerValue schemaVersion 2
        JSONObject intSchemaFields = new JSONObject();
        intSchemaFields.put("schemaVersion", new JSONObject().put("integerValue", 2));
        assertEquals(2, FirestoreDocumentUtil.getInt(intSchemaFields, "schemaVersion", 1));
    }
}
