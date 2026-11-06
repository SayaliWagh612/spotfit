package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.OfferingStatus;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.dao.*;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.service.SubmissionService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubmissionControllerTest {

    @Mock private BusinessDao businessDao;
    @Mock private SpaceDao spaceDao;
    @Mock private ResourceDao resourceDao;
    @Mock private OfferingDao offeringDao;
    @Mock private AvailabilityDao availabilityDao;

    private SubmissionService submissionService;
    private SubmissionController submissionController;
    private AutoCloseable closeable;

    private final String ownerId = "owner-999";
    private final String idToken = "mock-id-token";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        submissionService = new SubmissionService();
        submissionController = new SubmissionController(
                submissionService, businessDao, spaceDao, resourceDao, offeringDao, availabilityDao
        );
        AppSession.startSession(ownerId, "owner@spotfit.com", "Owner Name", "OWNER", idToken, "mock-refresh-token", 3600);
    }

    @AfterEach
    void tearDown() throws Exception {
        AppSession.clearSession();
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    @DisplayName("Submit Business transitions DRAFT business to SUBMITTED status")
    void testSubmitBusiness_DraftToSubmitted() {
        Business draftBiz = createValidBusiness("biz-1", ownerId, BusinessStatus.DRAFT);
        when(businessDao.getBusiness(eq("biz-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(draftBiz)));

        Business submittedBiz = createValidBusiness("biz-1", ownerId, BusinessStatus.SUBMITTED);
        submittedBiz.setSubmittedAt(10000L);

        when(businessDao.submitBusiness(eq("biz-1"), anyLong(), anyLong(), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(submittedBiz)));

        OperationResult<Business> result = submissionController.submitBusiness("biz-1").join();

        assertTrue(result.isSuccess());
        assertEquals(BusinessStatus.SUBMITTED, result.getData().getVerificationStatus());
        verify(businessDao).submitBusiness(eq("biz-1"), anyLong(), anyLong(), eq(idToken));
    }

    @Test
    @DisplayName("Submit Business transitions REJECTED business to SUBMITTED and clears rejection metadata")
    void testSubmitBusiness_RejectedResubmissionClearsRejectionMetadata() {
        Business rejectedBiz = createValidBusiness("biz-1", ownerId, BusinessStatus.REJECTED);
        rejectedBiz.setReviewedAt(5000L);
        rejectedBiz.setRejectionReason("Invalid address");

        when(businessDao.getBusiness(eq("biz-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(rejectedBiz)));

        ArgumentCaptor<Long> submittedAtCaptor = ArgumentCaptor.forClass(Long.class);
        Business resubmittedBiz = createValidBusiness("biz-1", ownerId, BusinessStatus.SUBMITTED);
        resubmittedBiz.setSubmittedAt(12000L);
        resubmittedBiz.setReviewedAt(0L);
        resubmittedBiz.setRejectionReason("");

        when(businessDao.submitBusiness(eq("biz-1"), submittedAtCaptor.capture(), anyLong(), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(resubmittedBiz)));

        OperationResult<Business> result = submissionController.submitBusiness("biz-1").join();

        assertTrue(result.isSuccess());
        assertEquals(BusinessStatus.SUBMITTED, result.getData().getVerificationStatus());
        assertEquals(0L, result.getData().getReviewedAt());
        assertEquals("", result.getData().getRejectionReason());
    }

    @Test
    @DisplayName("Submit Space fails when parent Business is not APPROVED")
    void testSubmitSpace_ParentBusinessNotApproved() {
        Space space = createValidSpace("space-1", "biz-1", ownerId, SpaceStatus.DRAFT);
        Business parentBiz = createValidBusiness("biz-1", ownerId, BusinessStatus.DRAFT);

        when(spaceDao.getSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));
        when(businessDao.getBusiness(eq("biz-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(parentBiz)));
        when(resourceDao.getResourcesForSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidResource("res-1", "space-1", ownerId, "biz-1")))));
        when(offeringDao.getOfferingsForSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidOffering("off-1", "space-1", ownerId, "biz-1")))));
        when(availabilityDao.getAvailabilityRules(eq("space-1"), eq("res-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidRule("rule-1", "space-1", "res-1", ownerId)))));

        OperationResult<Space> result = submissionController.submitSpace("space-1").join();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Parent Business must be APPROVED"));
        verify(spaceDao, never()).submitSpace(anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Submit Space succeeds when parent Business is APPROVED and all readiness criteria are met")
    void testSubmitSpace_Success() {
        Space draftSpace = createValidSpace("space-1", "biz-1", ownerId, SpaceStatus.DRAFT);
        Business approvedBiz = createValidBusiness("biz-1", ownerId, BusinessStatus.APPROVED);

        when(spaceDao.getSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(draftSpace)));
        when(businessDao.getBusiness(eq("biz-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(approvedBiz)));
        when(resourceDao.getResourcesForSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidResource("res-1", "space-1", ownerId, "biz-1")))));
        when(offeringDao.getOfferingsForSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidOffering("off-1", "space-1", ownerId, "biz-1")))));
        when(availabilityDao.getAvailabilityRules(eq("space-1"), eq("res-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidRule("rule-1", "space-1", "res-1", ownerId)))));

        Space submittedSpace = createValidSpace("space-1", "biz-1", ownerId, SpaceStatus.SUBMITTED);
        when(spaceDao.submitSpace(eq("space-1"), anyLong(), anyLong(), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(submittedSpace)));

        OperationResult<Space> result = submissionController.submitSpace("space-1").join();

        assertTrue(result.isSuccess());
        assertEquals(SpaceStatus.SUBMITTED, result.getData().getStatus());
        verify(spaceDao).submitSpace(eq("space-1"), anyLong(), anyLong(), eq(idToken));
    }

    @Test
    @DisplayName("Owner submission cannot set Business status to APPROVED")
    void testOwnerCannotSetBusinessApproved() {
        Business biz = createValidBusiness("biz-1", ownerId, BusinessStatus.DRAFT);
        when(businessDao.getBusiness(eq("biz-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(biz)));

        // Submission controller submits Business -> strictly to SUBMITTED, never APPROVED
        when(businessDao.submitBusiness(eq("biz-1"), anyLong(), anyLong(), eq(idToken)))
                .thenAnswer(inv -> {
                    Business res = createValidBusiness("biz-1", ownerId, BusinessStatus.SUBMITTED);
                    return CompletableFuture.completedFuture(OperationResult.success(res));
                });

        OperationResult<Business> result = submissionController.submitBusiness("biz-1").join();

        assertTrue(result.isSuccess());
        assertNotEquals(BusinessStatus.APPROVED, result.getData().getVerificationStatus());
        assertEquals(BusinessStatus.SUBMITTED, result.getData().getVerificationStatus());
    }

    @Test
    @DisplayName("Owner submission cannot set Space status to LIVE")
    void testOwnerCannotSetSpaceLive() {
        Space draftSpace = createValidSpace("space-1", "biz-1", ownerId, SpaceStatus.DRAFT);
        Business approvedBiz = createValidBusiness("biz-1", ownerId, BusinessStatus.APPROVED);

        when(spaceDao.getSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(draftSpace)));
        when(businessDao.getBusiness(eq("biz-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(approvedBiz)));
        when(resourceDao.getResourcesForSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidResource("res-1", "space-1", ownerId, "biz-1")))));
        when(offeringDao.getOfferingsForSpace(eq("space-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidOffering("off-1", "space-1", ownerId, "biz-1")))));
        when(availabilityDao.getAvailabilityRules(eq("space-1"), eq("res-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(createValidRule("rule-1", "space-1", "res-1", ownerId)))));

        when(spaceDao.submitSpace(eq("space-1"), anyLong(), anyLong(), eq(idToken)))
                .thenAnswer(inv -> {
                    Space res = createValidSpace("space-1", "biz-1", ownerId, SpaceStatus.SUBMITTED);
                    return CompletableFuture.completedFuture(OperationResult.success(res));
                });

        OperationResult<Space> result = submissionController.submitSpace("space-1").join();

        assertTrue(result.isSuccess());
        assertNotEquals(SpaceStatus.LIVE, result.getData().getStatus());
        assertEquals(SpaceStatus.SUBMITTED, result.getData().getStatus());
    }

    @Test
    @DisplayName("Owner cannot set Offering status to ACTIVE")
    void testOwnerCannotSetOfferingActive() {
        Offering off = createValidOffering("off-1", "space-1", ownerId, "biz-1");
        assertEquals(OfferingStatus.DRAFT, off.getStatus());
        assertNotEquals(OfferingStatus.ACTIVE, off.getStatus());
    }

    @Test
    @DisplayName("Stale DRAFT object cannot overwrite APPROVED state when fresh document is fetched")
    void testStaleDraftObjectCannotOverwriteApprovedState() {
        Business approvedBizInFirestore = createValidBusiness("biz-1", ownerId, BusinessStatus.APPROVED);

        when(businessDao.getBusiness(eq("biz-1"), eq(idToken)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(approvedBizInFirestore)));

        // Controller fetches fresh Business from DAO -> sees APPROVED -> readiness fails with "Business is already approved"
        OperationResult<SubmissionReadinessResult> readiness = submissionController.checkBusinessReadiness("biz-1").join();

        assertTrue(readiness.isSuccess());
        assertFalse(readiness.getData().isReady());
        assertTrue(readiness.getData().getMissingRequirements().stream()
                .anyMatch(s -> s.contains("Business is already approved")));
        verify(businessDao, never()).submitBusiness(anyString(), anyLong(), anyLong(), anyString());
    }

    private Business createValidBusiness(String bizId, String ownerId, String status) {
        return new Business(
                bizId, ownerId, "FitStudio", "FitStudio LLC",
                "info@fitstudio.com", "9876543210", "A fitness center",
                "123 Main St", "Near Park", "Mumbai", "Maharashtra", "400001",
                "REG123", status, true, 1000L, 1000L
        );
    }

    private Space createValidSpace(String spaceId, String bizId, String ownerId, String status) {
        return new Space(
                spaceId, bizId, ownerId, "Yoga Hall", "Quiet hall",
                "Fitness", "Yoga", "Studio", "123 Main St", "Park",
                "Mumbai", "Maharashtra", "400001", 30, List.of("AC", "Mats"),
                List.of("Yoga"), List.of("No shoes"), "06:00", "22:00",
                List.of("MONDAY", "TUESDAY", "WEDNESDAY"), "Asia/Kolkata",
                status, true, 1000L, 1000L
        );
    }

    private Resource createValidResource(String resId, String spaceId, String ownerId, String bizId) {
        return new Resource(
                resId, spaceId, bizId, ownerId, "Main Room", "Spacious room",
                ResourceType.ROOM, "RM-1", 30, List.of("Mats"), List.of("Audio"),
                "AVAILABLE", true, 1000L, 1000L
        );
    }

    private Offering createValidOffering(String offId, String spaceId, String ownerId, String bizId) {
        return new Offering(
                offId, spaceId, bizId, ownerId, "Yoga Class", "1h yoga session",
                "HOURLY", "PER_PERSON", 50000L, ResourceType.ROOM, 60, 60, 120, 30,
                20, 1, 30, List.of("Mat"), List.of("Arrive 5m early"),
                OfferingStatus.DRAFT, true, 1000L, 1000L
        );
    }

    private AvailabilityRule createValidRule(String ruleId, String spaceId, String resId, String ownerId) {
        return new AvailabilityRule(ruleId, spaceId, resId, ownerId, "MONDAY", "09:00", "17:00", 60, 60, 240, true, 1000L, 1000L);
    }
}
