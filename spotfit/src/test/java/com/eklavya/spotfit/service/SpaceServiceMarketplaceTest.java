package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceServiceMarketplaceTest {

    private SpaceDao mockSpaceDao;
    private BusinessDao mockBusinessDao;
    private SpaceService spaceService;

    @BeforeEach
    public void setUp() {
        mockSpaceDao = new SpaceDao() {
            @Override
            public CompletableFuture<OperationResult<List<Space>>> getLiveCreativitySpaces(String activityGroup, String idToken) {
                if ("FAIL_DAO".equals(activityGroup)) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NETWORK_ERROR, "Firestore query failed.")
                    );
                }
                List<Space> list = new ArrayList<>();

                Space s1 = new Space();
                s1.setSpaceId("s1");
                s1.setBusinessId("b1");
                s1.setStatus(SpaceStatus.LIVE);
                s1.setActive(true);
                s1.setCategory(SpaceCategory.CREATIVITY);
                s1.setActivityGroup(CreativityActivityGroup.ART);

                Space s2 = new Space();
                s2.setSpaceId("s2");
                s2.setBusinessId("b2_unapproved");
                s2.setStatus(SpaceStatus.LIVE);
                s2.setActive(true);
                s2.setCategory(SpaceCategory.CREATIVITY);
                s2.setActivityGroup(CreativityActivityGroup.ART);

                Space s3 = new Space();
                s3.setSpaceId("s3");
                s3.setBusinessId("b3_err");
                s3.setStatus(SpaceStatus.LIVE);
                s3.setActive(true);
                s3.setCategory(SpaceCategory.CREATIVITY);
                s3.setActivityGroup(CreativityActivityGroup.ART);

                list.add(s1);
                list.add(s2);
                list.add(s3);
                return CompletableFuture.completedFuture(OperationResult.success(list));
            }
        };

        mockBusinessDao = new BusinessDao() {
            @Override
            public CompletableFuture<OperationResult<Business>> getBusiness(String businessId, String idToken) {
                if ("b1".equals(businessId)) {
                    Business b = new Business();
                    b.setBusinessId("b1");
                    b.setActive(true);
                    b.setVerificationStatus(BusinessStatus.APPROVED);
                    return CompletableFuture.completedFuture(OperationResult.success(b));
                } else if ("b2_unapproved".equals(businessId)) {
                    Business b = new Business();
                    b.setBusinessId("b2_unapproved");
                    b.setActive(true);
                    b.setVerificationStatus(BusinessStatus.DRAFT);
                    return CompletableFuture.completedFuture(OperationResult.success(b));
                } else if ("b3_err".equals(businessId)) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NETWORK_ERROR, "Network timeout during business fetch.")
                    );
                }
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.NOT_FOUND, "Business not found.")
                );
            }
        };

        spaceService = new SpaceService(mockSpaceDao, mockBusinessDao);
    }

    @Test
    public void testGetLiveCreativitySpaces_InvalidGroup() throws Exception {
        OperationResult<List<Space>> res = spaceService.getLiveCreativitySpaces("INVALID", "token").get();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, res.getErrorCode());
    }

    @Test
    public void testGetLiveCreativitySpaces_MissingToken() throws Exception {
        OperationResult<List<Space>> res = spaceService.getLiveCreativitySpaces("ART", "").get();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.AUTHENTICATION_REQUIRED, res.getErrorCode());
    }

    @Test
    public void testGetLiveCreativitySpaces_GenuineNetworkErrorOnBusiness() throws Exception {
        // When business fetch fails with NETWORK_ERROR, operation fails!
        OperationResult<List<Space>> res = spaceService.getLiveCreativitySpaces("ART", "valid-token").get();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.NETWORK_ERROR, res.getErrorCode());
    }

    @Test
    public void testGetLiveCreativitySpaces_SuccessFiltering() throws Exception {
        // Custom test DAO without the error business
        SpaceDao cleanSpaceDao = new SpaceDao() {
            @Override
            public CompletableFuture<OperationResult<List<Space>>> getLiveCreativitySpaces(String activityGroup, String idToken) {
                List<Space> list = new ArrayList<>();
                Space s1 = new Space();
                s1.setSpaceId("s1");
                s1.setBusinessId("b1");
                s1.setStatus(SpaceStatus.LIVE);
                s1.setActive(true);
                s1.setCategory(SpaceCategory.CREATIVITY);
                s1.setActivityGroup(CreativityActivityGroup.ART);

                Space s2 = new Space();
                s2.setSpaceId("s2");
                s2.setBusinessId("b2_unapproved");
                s2.setStatus(SpaceStatus.LIVE);
                s2.setActive(true);
                s2.setCategory(SpaceCategory.CREATIVITY);
                s2.setActivityGroup(CreativityActivityGroup.ART);

                list.add(s1);
                list.add(s2);
                return CompletableFuture.completedFuture(OperationResult.success(list));
            }
        };

        SpaceService cleanService = new SpaceService(cleanSpaceDao, mockBusinessDao);
        OperationResult<List<Space>> res = cleanService.getLiveCreativitySpaces("ART", "valid-token").get();
        assertTrue(res.isSuccess());
        assertEquals(1, res.getData().size());
        assertEquals("s1", res.getData().get(0).getSpaceId());
    }
}
