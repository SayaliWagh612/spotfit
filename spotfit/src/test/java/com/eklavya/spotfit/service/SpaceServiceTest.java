package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceServiceTest {

    private SpaceService spaceService;
    private MockSpaceDao mockSpaceDao;

    @BeforeEach
    public void setUp() {
        mockSpaceDao = new MockSpaceDao();
        spaceService = new SpaceService(mockSpaceDao);
    }

    @Test
    public void testValidSpaceInputsValidation() {
        OperationResult<Void> result = spaceService.validateSpaceInputs(
                "DAWN Podcast Studio",
                "A soundproof podcasting room with pro mics.",
                SpaceCategory.CREATIVITY,
                CreativityActivityGroup.PODCASTS,
                "Podcast Studio",
                "101 Media Hub",
                "Mumbai",
                "Maharashtra",
                "400001",
                10,
                "08:00 AM",
                "08:00 PM",
                Arrays.asList("Mon", "Tue", "Wed")
        );
        assertTrue(result.isSuccess());
    }

    @Test
    public void testBlankSpaceNameRejected() {
        OperationResult<Void> result = spaceService.validateSpaceInputs(
                "",
                "A soundproof podcasting room.",
                SpaceCategory.CREATIVITY,
                CreativityActivityGroup.PODCASTS,
                "Podcast Studio",
                "101 Media Hub",
                "Mumbai",
                "Maharashtra",
                "400001",
                10,
                "08:00 AM",
                "08:00 PM",
                Arrays.asList("Mon")
        );
        assertTrue(result.isFailure());
        assertEquals("SPACE_NAME_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testInvalidCapacityRejected() {
        OperationResult<Void> result = spaceService.validateSpaceInputs(
                "DAWN Studio",
                "Description",
                SpaceCategory.CREATIVITY,
                CreativityActivityGroup.PODCASTS,
                "Podcast Studio",
                "101 Media Hub",
                "Mumbai",
                "Maharashtra",
                "400001",
                0,
                "08:00 AM",
                "08:00 PM",
                Arrays.asList("Mon")
        );
        assertTrue(result.isFailure());
        assertEquals("CAPACITY_INVALID", result.getErrorCode());
    }

    @Test
    public void testMissingOperatingDaysRejected() {
        OperationResult<Void> result = spaceService.validateSpaceInputs(
                "DAWN Studio",
                "Description",
                SpaceCategory.CREATIVITY,
                CreativityActivityGroup.PODCASTS,
                "Podcast Studio",
                "101 Media Hub",
                "Mumbai",
                "Maharashtra",
                "400001",
                10,
                "08:00 AM",
                "08:00 PM",
                Collections.emptyList()
        );
        assertTrue(result.isFailure());
        assertEquals("OPERATING_DAYS_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testInvalidActivityGroupForCreativityRejected() {
        OperationResult<Void> result = spaceService.validateSpaceInputs(
                "DAWN Studio",
                "Description",
                SpaceCategory.CREATIVITY,
                "INVALID_GROUP",
                "Podcast Studio",
                "101 Media Hub",
                "Mumbai",
                "Maharashtra",
                "400001",
                10,
                "08:00 AM",
                "08:00 PM",
                Arrays.asList("Mon")
        );
        assertTrue(result.isFailure());
        assertEquals("ACTIVITY_GROUP_INVALID", result.getErrorCode());
    }

    @Test
    public void testSpaceCreationAssignsUUIDAndDraftStatus() throws Exception {
        OperationResult<Space> result = spaceService.createSpace(
                "owner123",
                "biz123",
                "DAWN Studio",
                "Description",
                SpaceCategory.CREATIVITY,
                CreativityActivityGroup.PODCASTS,
                "Podcast Studio",
                "101 Media Hub",
                "Landmark",
                "Mumbai",
                "Maharashtra",
                "400001",
                10,
                Arrays.asList("Wi-Fi"),
                Arrays.asList("Podcasting"),
                Arrays.asList("No smoking"),
                "08:00 AM",
                "08:00 PM",
                Arrays.asList("Mon"),
                "mock-id-token"
        ).get();

        assertTrue(result.isSuccess());
        Space space = result.getData();
        assertNotNull(space);
        assertNotNull(space.getSpaceId());
        assertFalse(space.getSpaceId().isBlank());
        assertEquals(SpaceStatus.DRAFT, space.getStatus());
        assertTrue(space.isActive());
        assertEquals("owner123", space.getOwnerId());
        assertEquals("biz123", space.getBusinessId());
        assertTrue(space.getCreatedAt() > 0);
    }

    @Test
    public void testUpdateNonDraftSpaceRejected() throws Exception {
        Space space = new Space();
        space.setSpaceId("space123");
        space.setSpaceName("Non-Draft Space");
        space.setDescription("Desc");
        space.setCategory(SpaceCategory.CREATIVITY);
        space.setActivityGroup(CreativityActivityGroup.PODCASTS);
        space.setSpaceType("Podcast Studio");
        space.setAddressLine("101 Hub");
        space.setCity("Mumbai");
        space.setState("Maharashtra");
        space.setPostalCode("400001");
        space.setCapacity(10);
        space.setOpeningTime("08:00 AM");
        space.setClosingTime("08:00 PM");
        space.setOperatingDays(Arrays.asList("Mon"));
        space.setStatus(SpaceStatus.LIVE);

        OperationResult<Space> result = spaceService.updateSpace(space, "mock-token").get();
        assertTrue(result.isFailure());
        assertEquals("Only DRAFT spaces can be edited.", result.getMessage());
    }

    @Test
    public void testSingleFieldEditPreservesAllUnchangedAttributes() throws Exception {
        Space original = new Space();
        original.setSpaceId("space_123");
        original.setBusinessId("biz_456");
        original.setOwnerId("owner_789");
        original.setSpaceName("Original Space Name");
        original.setDescription("Original Description");
        original.setCategory(SpaceCategory.CREATIVITY);
        original.setActivityGroup(CreativityActivityGroup.PODCASTS);
        original.setSpaceType("Podcast Studio");
        original.setAddressLine("101 Media Hub");
        original.setLandmark("Near Metro");
        original.setCity("Mumbai");
        original.setState("Maharashtra");
        original.setPostalCode("400001");
        original.setCapacity(25);
        original.setAmenities(Arrays.asList("Wi-Fi", "Air Conditioning", "Green Screen"));
        original.setSuitableActivities(Arrays.asList("Podcasting", "Reels Shooting"));
        original.setRules(Arrays.asList("No Smoking", "No Food"));
        original.setOpeningTime("09:00 AM");
        original.setClosingTime("09:00 PM");
        original.setOperatingDays(Arrays.asList("Mon", "Tue", "Wed"));
        original.setStatus(SpaceStatus.DRAFT);
        original.setActive(true);
        original.setCreatedAt(1700000000000L);

        // Simulate changing ONLY the Space Name
        Space updatedInput = new Space();
        updatedInput.setSpaceId(original.getSpaceId());
        updatedInput.setBusinessId(original.getBusinessId());
        updatedInput.setOwnerId(original.getOwnerId());
        updatedInput.setSpaceName("Updated DAWN Podcast Studio"); // Modified
        updatedInput.setDescription(original.getDescription());
        updatedInput.setCategory(original.getCategory());
        updatedInput.setActivityGroup(original.getActivityGroup());
        updatedInput.setSpaceType(original.getSpaceType());
        updatedInput.setAddressLine(original.getAddressLine());
        updatedInput.setLandmark(original.getLandmark());
        updatedInput.setCity(original.getCity());
        updatedInput.setState(original.getState());
        updatedInput.setPostalCode(original.getPostalCode());
        updatedInput.setCapacity(original.getCapacity());
        updatedInput.setAmenities(original.getAmenities());
        updatedInput.setSuitableActivities(original.getSuitableActivities());
        updatedInput.setRules(original.getRules());
        updatedInput.setOpeningTime(original.getOpeningTime());
        updatedInput.setClosingTime(original.getClosingTime());
        updatedInput.setOperatingDays(original.getOperatingDays());
        updatedInput.setStatus(original.getStatus());
        updatedInput.setActive(original.isActive());
        updatedInput.setCreatedAt(original.getCreatedAt());

        OperationResult<Space> res = spaceService.updateSpace(updatedInput, "mock-token").get();
        assertTrue(res.isSuccess());
        Space resSpace = res.getData();

        assertEquals("Updated DAWN Podcast Studio", resSpace.getSpaceName());
        assertEquals("Original Description", resSpace.getDescription());
        assertEquals(25, resSpace.getCapacity());
        assertEquals(3, resSpace.getAmenities().size());
        assertTrue(resSpace.getAmenities().contains("Green Screen"));
        assertEquals(2, resSpace.getSuitableActivities().size());
        assertEquals(2, resSpace.getRules().size());
        assertEquals(3, resSpace.getOperatingDays().size());
        assertEquals("space_123", resSpace.getSpaceId());
        assertEquals("owner_789", resSpace.getOwnerId());
        assertEquals(SpaceStatus.DRAFT, resSpace.getStatus());
        assertTrue(resSpace.getUpdatedAt() > 0);
    }

    // Mock DAO helper
    private static class MockSpaceDao extends SpaceDao {
        @Override
        public CompletableFuture<OperationResult<Space>> createSpace(Space space, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(space));
        }

        @Override
        public CompletableFuture<OperationResult<Space>> updateSpace(Space space, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(space));
        }
    }
}
