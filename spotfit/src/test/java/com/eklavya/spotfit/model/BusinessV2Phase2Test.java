package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.service.BusinessService;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessV2Phase2Test {

    private BusinessDao businessDao;
    private BusinessService businessService;

    @BeforeEach
    public void setUp() {
        businessDao = new BusinessDao();
        businessService = new BusinessService(businessDao);
    }

    @Test
    @DisplayName("Req 1: Field count derived from reflection equals 25 (20 V1 fields + 5 V2 fields)")
    public void testFieldCountDerivedFromReflection() {
        Field[] fields = Business.class.getDeclaredFields();
        assertEquals(25, fields.length, "Business must declare exactly 25 fields (20 V1 fields + 5 V2 fields)");
    }

    @Test
    @DisplayName("Req 2: Legacy constructor signatures still exist")
    public void testLegacyConstructorSignaturesExist() throws NoSuchMethodException {
        Constructor<Business> noArg = Business.class.getConstructor();
        assertNotNull(noArg);

        Constructor<Business> c17 = Business.class.getConstructor(
                String.class, String.class, String.class, String.class,
                String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, boolean.class,
                long.class, long.class
        );
        assertNotNull(c17);

        Constructor<Business> c20 = Business.class.getConstructor(
                String.class, String.class, String.class, String.class,
                String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, boolean.class,
                long.class, long.class, String.class,
                long.class, long.class
        );
        assertNotNull(c20);

        Constructor<Business> c25 = Business.class.getConstructor(
                int.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, List.class,
                String.class, String.class, String.class, boolean.class,
                long.class, long.class, String.class,
                long.class, long.class
        );
        assertNotNull(c25);
    }

    @Test
    @DisplayName("Req 3: Legacy constructor distinct sentinel values map to correct fields")
    public void testLegacyConstructorDistinctSentinelValues() {
        Business b = new Business(
                "SENTINEL_BIZ_ID", "SENTINEL_OWNER_ID", "SENTINEL_NAME", "SENTINEL_LEGAL",
                "SENTINEL_EMAIL@TEST.COM", "SENTINEL_PHONE", "SENTINEL_DESC",
                "SENTINEL_ADDR", "SENTINEL_LANDMARK", "SENTINEL_CITY", "SENTINEL_STATE", "SENTINEL_POSTAL",
                "SENTINEL_REG", BusinessStatus.SUBMITTED, false,
                1111L, 2222L, "SENTINEL_REJECTION",
                3333L, 4444L
        );

        assertEquals("SENTINEL_BIZ_ID", b.getBusinessId());
        assertEquals("SENTINEL_OWNER_ID", b.getOwnerId());
        assertEquals("SENTINEL_NAME", b.getBusinessName());
        assertEquals("SENTINEL_LEGAL", b.getLegalName());
        assertEquals("SENTINEL_EMAIL@TEST.COM", b.getBusinessEmail());
        assertEquals("SENTINEL_PHONE", b.getBusinessPhone());
        assertEquals("SENTINEL_DESC", b.getDescription());
        assertEquals("SENTINEL_ADDR", b.getAddressLine());
        assertEquals("SENTINEL_LANDMARK", b.getLandmark());
        assertEquals("SENTINEL_CITY", b.getCity());
        assertEquals("SENTINEL_STATE", b.getState());
        assertEquals("SENTINEL_POSTAL", b.getPostalCode());
        assertEquals("SENTINEL_REG", b.getRegistrationNumber());
        assertEquals(BusinessStatus.SUBMITTED, b.getVerificationStatus());
        assertFalse(b.isActive());
        assertEquals(1111L, b.getSubmittedAt());
        assertEquals(2222L, b.getReviewedAt());
        assertEquals("SENTINEL_REJECTION", b.getRejectionReason());
        assertEquals(3333L, b.getCreatedAt());
        assertEquals(4444L, b.getUpdatedAt());
        assertEquals(1, b.getSchemaVersion(), "Legacy constructor must set schemaVersion = 1");
    }

    @Test
    @DisplayName("Req 4: V2 constructor/factory values map to correct fields")
    public void testV2ConstructorAndFactoryDistinctSentinelValues() {
        List<String> groups = List.of(CreativityActivityGroup.MUSIC, CreativityActivityGroup.ART);
        Business b = Business.createV2(
                "BIZ_V2", "OWNER_V2", "NAME_V2", "LEGAL_V2",
                "EMAIL_V2@TEST.COM", "PHONE_V2", "DESC_V2",
                "ADDR_V2", "LANDMARK_V2", "CITY_V2", "STATE_V2", "POSTAL_V2",
                "REG_V2", SpaceCategory.CREATIVITY, groups,
                "https://logo.com/v2.png", "https://website.com/v2",
                BusinessStatus.DRAFT, true,
                5555L, 6666L, "REASON_V2",
                7777L, 8888L
        );

        assertEquals(2, b.getSchemaVersion());
        assertEquals("BIZ_V2", b.getBusinessId());
        assertEquals("OWNER_V2", b.getOwnerId());
        assertEquals("NAME_V2", b.getBusinessName());
        assertEquals("LEGAL_V2", b.getLegalName());
        assertEquals("EMAIL_V2@TEST.COM", b.getBusinessEmail());
        assertEquals("PHONE_V2", b.getBusinessPhone());
        assertEquals("DESC_V2", b.getDescription());
        assertEquals("ADDR_V2", b.getAddressLine());
        assertEquals("LANDMARK_V2", b.getLandmark());
        assertEquals("CITY_V2", b.getCity());
        assertEquals("STATE_V2", b.getState());
        assertEquals("POSTAL_V2", b.getPostalCode());
        assertEquals("REG_V2", b.getRegistrationNumber());
        assertEquals(SpaceCategory.CREATIVITY, b.getCategory());
        assertEquals(2, b.getActivityGroups().size());
        assertEquals("https://logo.com/v2.png", b.getLogoUrl());
        assertEquals("https://website.com/v2", b.getWebsite());
        assertEquals(BusinessStatus.DRAFT, b.getVerificationStatus());
        assertTrue(b.isActive());
        assertEquals(5555L, b.getSubmittedAt());
        assertEquals(6666L, b.getReviewedAt());
        assertEquals("REASON_V2", b.getRejectionReason());
        assertEquals(7777L, b.getCreatedAt());
        assertEquals(8888L, b.getUpdatedAt());
    }

    @Test
    @DisplayName("Req 5: Legacy constructors produce schema version 1")
    public void testLegacyConstructorsProduceSchemaVersion1() {
        Business b1 = new Business();
        assertEquals(1, b1.getSchemaVersion());

        Business b2 = new Business("b", "o", "n", "l", "e@b.com", "9876543210", "d", "a", "l", "c", "s", "411001", "r", BusinessStatus.DRAFT, true, 100L, 200L);
        assertEquals(1, b2.getSchemaVersion());
    }

    @Test
    @DisplayName("Req 6: Explicit V2 creation produces schema version 2")
    public void testExplicitV2CreationProducesSchemaVersion2() {
        Business b = Business.createV2("b2", "o2", "n2", "l2", "e2@b.com", "9876543210", "d2", "a2", "l2", "c2", "s2", "411001", "r2", SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.ART), "", "", BusinessStatus.DRAFT, true, 0L, 0L, "", 100L, 200L);
        assertEquals(2, b.getSchemaVersion());
    }

    @Test
    @DisplayName("Deserialization: Legacy document loading without V2 fields defaults to schemaVersion 1")
    public void testLegacyDocumentLoadingWithoutV2Fields() {
        Business v1 = new Business("b1", "o1", "V1 Biz", "Legal", "v1@test.com", "9876543210", "Desc", "Addr", "Mark", "Pune", "Maharashtra", "411001", "REG", BusinessStatus.DRAFT, true, 100L, 200L);
        JSONObject docV1 = businessDao.toFirestoreDocument(v1);
        
        Business restored = businessDao.fromFirestoreDocument(docV1);
        assertNotNull(restored);
        assertEquals(1, restored.getSchemaVersion());
        assertEquals("", restored.getCategory());
        assertTrue(restored.getActivityGroups().isEmpty());
        assertEquals("", restored.getLogoUrl());
        assertEquals("", restored.getWebsite());
    }

    @Test
    @DisplayName("Deserialization: Document with schemaVersion 1 restores correctly")
    public void testLegacyDocumentWithSchemaVersion1() {
        Business v1 = new Business(1, "b1", "o1", "V1 Biz", "Legal", "v1@test.com", "9876543210", "Desc", "Addr", "Mark", "Pune", "Maharashtra", "411001", "REG", "", List.of(), "", "", BusinessStatus.DRAFT, true, 0L, 0L, "", 100L, 200L);
        JSONObject docV1 = businessDao.toFirestoreDocument(v1);

        Business restored = businessDao.fromFirestoreDocument(docV1);
        assertNotNull(restored);
        assertEquals(1, restored.getSchemaVersion());
    }

    @Test
    @DisplayName("Deserialization: V2 document loading with all fields restores correctly")
    public void testV2DocumentLoadingWithAllFields() {
        Business v2 = createSampleV2CreativityBusiness();
        JSONObject docV2 = businessDao.toFirestoreDocument(v2);

        Business restored = businessDao.fromFirestoreDocument(docV2);
        assertNotNull(restored);
        assertEquals(2, restored.getSchemaVersion());
        assertEquals(SpaceCategory.CREATIVITY, restored.getCategory());
        assertEquals(2, restored.getActivityGroups().size());
        assertTrue(restored.getActivityGroups().contains(CreativityActivityGroup.ART));
        assertTrue(restored.getActivityGroups().contains(CreativityActivityGroup.MUSIC));
        assertEquals("https://example.com/logo.png", restored.getLogoUrl());
        assertEquals("https://example.com", restored.getWebsite());
    }

    @Test
    @DisplayName("Serialization: V1 serialization excludes V2 fields")
    public void testV1SerializationExcludesV2Fields() {
        Business v1 = new Business("b1", "o1", "V1 Biz", "Legal", "v1@test.com", "9876543210", "Desc", "Addr", "Mark", "Pune", "Maharashtra", "411001", "REG", BusinessStatus.DRAFT, true, 100L, 200L);
        JSONObject docV1 = businessDao.toFirestoreDocument(v1);
        JSONObject fieldsV1 = FirestoreDocumentUtil.getFields(docV1);
        assertEquals(20, fieldsV1.length(), "V1 Firestore fields map must contain exactly 20 V1 fields");
        assertFalse(fieldsV1.has("schemaVersion"));
        assertFalse(fieldsV1.has("category"));
        assertFalse(fieldsV1.has("activityGroups"));
        assertFalse(fieldsV1.has("logoUrl"));
        assertFalse(fieldsV1.has("website"));
    }

    @Test
    @DisplayName("Serialization: V2 serialization includes all V2 fields")
    public void testV2SerializationIncludesAllFields() {
        Business v2 = createSampleV2CreativityBusiness();
        JSONObject docV2 = businessDao.toFirestoreDocument(v2);
        JSONObject fieldsV2 = FirestoreDocumentUtil.getFields(docV2);
        assertEquals(25, fieldsV2.length(), "V2 Firestore fields map must contain exactly 25 fields");
        assertTrue(fieldsV2.has("schemaVersion"));
        assertEquals(2, FirestoreDocumentUtil.getInt(fieldsV2, "schemaVersion", 0));
        assertTrue(fieldsV2.has("category"));
        assertEquals(SpaceCategory.CREATIVITY, FirestoreDocumentUtil.getString(fieldsV2, "category"));
        assertTrue(fieldsV2.has("activityGroups"));
        assertTrue(fieldsV2.getJSONObject("activityGroups").has("arrayValue"));
        assertTrue(fieldsV2.has("logoUrl"));
        assertTrue(fieldsV2.has("website"));
    }

    @Test
    @DisplayName("Mutation Testing: Defensive copying of activityGroups prevents external mutation")
    public void testDefensiveCopyingActivityGroups() {
        List<String> mutableList = new ArrayList<>();
        mutableList.add(CreativityActivityGroup.ART);

        Business b = Business.createV2("b1", "o1", "Name", "Legal", "e@b.com", "9876543210", "Desc", "Addr", "Mark", "Pune", "Maharashtra", "411001", "REG", SpaceCategory.CREATIVITY, mutableList, "", "", BusinessStatus.DRAFT, true, 0L, 0L, "", 100L, 200L);
        
        // Mutate original input list
        mutableList.add(CreativityActivityGroup.MUSIC);
        assertEquals(1, b.getActivityGroups().size(), "Business activityGroups list must not mutate when input list is modified");

        // Attempt to mutate getter output list
        assertThrows(UnsupportedOperationException.class, () -> {
            b.getActivityGroups().add(CreativityActivityGroup.PODCASTS);
        }, "Getter output list must be unmodifiable");
    }

    @Test
    @DisplayName("Validation: Null Business object fails V2 validation")
    public void testValidateV2BusinessNullBusiness() {
        SubmissionReadinessResult result = businessService.validateV2Business(null, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Business object cannot be null."));
    }

    @Test
    @DisplayName("Validation: Business schema version must be 2")
    public void testValidateV2BusinessSchemaVersionMustBe2() {
        Business v1 = new Business("b1", "o1", "V1 Biz", "Legal", "v1@test.com", "9876543210", "Desc", "Addr", "Mark", "Pune", "Maharashtra", "411001", "REG", BusinessStatus.DRAFT, true, 100L, 200L);
        SubmissionReadinessResult result = businessService.validateV2Business(v1, "o1");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Business schema version must be 2."));
    }

    @Test
    @DisplayName("Validation: Unauthenticated owner ID fails V2 validation")
    public void testValidateV2BusinessUnauthenticatedOwnerId() {
        Business v2 = createSampleV2CreativityBusiness();
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Authenticated Owner ID is required."));
    }

    @Test
    @DisplayName("Validation: Business owner ID mismatch fails V2 validation")
    public void testValidateV2BusinessOwnerMismatch() {
        Business v2 = createSampleV2CreativityBusiness();
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "wrong-owner-id");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Business owner ID does not match authenticated owner."));
    }

    @Test
    @DisplayName("Validation: Inactive business fails V2 validation")
    public void testValidateV2BusinessInactiveBusiness() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setActive(false);
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Business is not active."));
    }

    @Test
    @DisplayName("Validation: Invalid category fails V2 validation")
    public void testValidateV2BusinessInvalidCategory() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setCategory("INVALID_CATEGORY_XYZ");
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Invalid business category."));
    }

    @Test
    @DisplayName("Validation: Missing activity groups fails V2 validation")
    public void testValidateV2BusinessMissingActivityGroups() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setActivityGroups(List.of());
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Activity groups are required."));
    }

    @Test
    @DisplayName("Validation: Blank activity group fails V2 validation")
    public void testValidateV2BusinessBlankActivityGroup() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setActivityGroups(List.of(CreativityActivityGroup.ART, "   "));
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Activity group cannot be blank."));
    }

    @Test
    @DisplayName("Validation: Duplicate activity group fails V2 validation")
    public void testValidateV2BusinessDuplicateActivityGroup() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setActivityGroups(List.of(CreativityActivityGroup.ART, CreativityActivityGroup.ART));
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().stream().anyMatch(e -> e.contains("Duplicate activity group")));
    }

    @Test
    @DisplayName("Validation: Invalid activity group for CREATIVITY category fails V2 validation")
    public void testValidateV2BusinessInvalidCreativityActivityGroup() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setActivityGroups(List.of(CreativityActivityGroup.ART, "FOOTBALL_PLAY"));
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().stream().anyMatch(e -> e.contains("Invalid activity group for CREATIVITY category")));
    }

    @Test
    @DisplayName("Validation: Valid Creativity V2 business passes validation")
    public void testValidateV2BusinessValidCreativityBusiness() {
        Business v2 = createSampleV2CreativityBusiness();
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertTrue(result.isReady());
        assertTrue(result.getMissingRequirements().isEmpty());
    }

    @Test
    @DisplayName("Validation: WELLNESS category rejected in V2 MVP")
    public void testValidateV2BusinessWellnessCategoryRejectedInMvp() {
        Business bWellness = createSampleV2CreativityBusiness();
        bWellness.setCategory(SpaceCategory.WELLNESS);
        SubmissionReadinessResult rWellness = businessService.validateV2Business(bWellness, "owner-001");
        assertFalse(rWellness.isReady());
        assertTrue(rWellness.getMissingRequirements().contains("Owner V2 currently supports the Creativity category only."));
    }

    @Test
    @DisplayName("Validation: SPORTS category rejected in V2 MVP")
    public void testValidateV2BusinessSportsCategoryRejectedInMvp() {
        Business bSports = createSampleV2CreativityBusiness();
        bSports.setCategory(SpaceCategory.SPORTS);
        SubmissionReadinessResult rSports = businessService.validateV2Business(bSports, "owner-001");
        assertFalse(rSports.isReady());
        assertTrue(rSports.getMissingRequirements().contains("Owner V2 currently supports the Creativity category only."));
    }

    @Test
    @DisplayName("Validation: STUDY_WORKSPACE category rejected in V2 MVP")
    public void testValidateV2BusinessStudyWorkspaceCategoryRejectedInMvp() {
        assertEquals("STUDY_WORKSPACE", SpaceCategory.STUDY_WORKSPACE);
        Business bStudy = createSampleV2CreativityBusiness();
        bStudy.setCategory(SpaceCategory.STUDY_WORKSPACE);
        SubmissionReadinessResult rStudy = businessService.validateV2Business(bStudy, "owner-001");
        assertFalse(rStudy.isReady());
        assertTrue(rStudy.getMissingRequirements().contains("Owner V2 currently supports the Creativity category only."));
    }

    @Test
    @DisplayName("Validation: Invalid website URL format fails V2 validation")
    public void testValidateV2BusinessInvalidWebsiteUrl() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setWebsite("ftp://example.com");
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Website URL must start with http:// or https://"));
    }

    @Test
    @DisplayName("Validation: Invalid logo URL format fails V2 validation")
    public void testValidateV2BusinessInvalidLogoUrl() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setLogoUrl("invalid-path/logo.png");
        SubmissionReadinessResult result = businessService.validateV2Business(v2, "owner-001");
        assertFalse(result.isReady());
        assertTrue(result.getMissingRequirements().contains("Logo URL must start with http:// or https://"));
    }

    @Test
    @DisplayName("Validation: Owner cannot validate business in APPROVED / UNDER_REVIEW / SUSPENDED status")
    public void testValidateV2BusinessNonDraftStatus() {
        Business v2 = createSampleV2CreativityBusiness();
        v2.setVerificationStatus(BusinessStatus.APPROVED);
        SubmissionReadinessResult rApproved = businessService.validateV2Business(v2, "owner-001");
        assertFalse(rApproved.isReady());
        assertTrue(rApproved.getMissingRequirements().stream().anyMatch(e -> e.contains("Owner cannot edit or submit business in status")));

        v2.setVerificationStatus(BusinessStatus.UNDER_REVIEW);
        SubmissionReadinessResult rReview = businessService.validateV2Business(v2, "owner-001");
        assertFalse(rReview.isReady());

        v2.setVerificationStatus(BusinessStatus.SUSPENDED);
        SubmissionReadinessResult rSuspended = businessService.validateV2Business(v2, "owner-001");
        assertFalse(rSuspended.isReady());
    }

    @Test
    @DisplayName("Req 13: createV2Business assigns authoritative defaults correctly")
    public void testCreateV2BusinessAuthoritativeDefaults() {
        BusinessDao mockDao = new BusinessDao() {
            @Override
            public CompletableFuture<OperationResult<Business>> createBusiness(Business business, String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(business));
            }
        };

        BusinessService customService = new BusinessService(mockDao);

        OperationResult<Business> result = customService.createV2Business(
                "auth-owner-77", "New V2 Business", "Legal Name", "test@v2.com", "9876543210",
                "Description", "123 Main St", "Landmark", "Pune", "Maharashtra", "411001",
                "REG-123", SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.MUSIC),
                "https://logo.com/img.png", "https://website.com", "mock-token"
        ).join();

        assertTrue(result.isSuccess());
        Business bCreated = result.getData();
        assertNotNull(bCreated);
        assertNotNull(bCreated.getBusinessId());
        assertFalse(bCreated.getBusinessId().isBlank());
        assertEquals("auth-owner-77", bCreated.getOwnerId());
        assertEquals(2, bCreated.getSchemaVersion());
        assertEquals(BusinessStatus.DRAFT, bCreated.getVerificationStatus());
        assertTrue(bCreated.isActive());
        assertEquals(0L, bCreated.getSubmittedAt());
        assertEquals(0L, bCreated.getReviewedAt());
        assertEquals("", bCreated.getRejectionReason());
        assertTrue(bCreated.getCreatedAt() > 0);
        assertEquals(bCreated.getCreatedAt(), bCreated.getUpdatedAt());
    }

    private Business createSampleV2CreativityBusiness() {
        return Business.createV2(
                "biz-001", "owner-001", "Sample V2 Creativity Business", "Sample LLC",
                "sample@business.com", "9876543210", "Description text",
                "Address Line 1", "Landmark 1", "Pune", "Maharashtra", "411001",
                "REG-999", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART, CreativityActivityGroup.MUSIC),
                "https://example.com/logo.png", "https://example.com",
                BusinessStatus.DRAFT, true, 0L, 0L, "", 1000L, 2000L
        );
    }
}
