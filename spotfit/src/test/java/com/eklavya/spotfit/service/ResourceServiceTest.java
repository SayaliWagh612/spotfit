package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.ResourceStatus;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceServiceTest {

    private ResourceService resourceService;

    @BeforeEach
    public void setUp() {
        resourceService = new ResourceService();
    }

    @Test
    public void testValidResourceInputs() {
        OperationResult<Void> result = resourceService.validateResourceInputs(
                "Podcast Room A",
                "ROOM-A",
                ResourceType.ROOM,
                4
        );
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMissingResourceName() {
        OperationResult<Void> result = resourceService.validateResourceInputs(
                "",
                "ROOM-A",
                ResourceType.ROOM,
                4
        );
        assertTrue(result.isFailure());
        assertEquals("RESOURCE_NAME_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testMissingResourceCode() {
        OperationResult<Void> result = resourceService.validateResourceInputs(
                "Podcast Room A",
                "",
                ResourceType.ROOM,
                4
        );
        assertTrue(result.isFailure());
        assertEquals("RESOURCE_CODE_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testInvalidResourceType() {
        OperationResult<Void> result = resourceService.validateResourceInputs(
                "Podcast Room A",
                "ROOM-A",
                "INVALID_TYPE",
                4
        );
        assertTrue(result.isFailure());
        assertEquals("RESOURCE_TYPE_INVALID", result.getErrorCode());
    }

    @Test
    public void testInvalidCapacity() {
        OperationResult<Void> result = resourceService.validateResourceInputs(
                "Podcast Room A",
                "ROOM-A",
                ResourceType.ROOM,
                0
        );
        assertTrue(result.isFailure());
        assertEquals("CAPACITY_INVALID", result.getErrorCode());
    }

    @Test
    public void testResourceDefaultValues() {
        Resource resource = new Resource();
        assertEquals(ResourceStatus.AVAILABLE, resource.getStatus());
        assertTrue(resource.isActive());
        assertNotNull(resource.getEquipment());
        assertNotNull(resource.getAmenities());
    }

    @Test
    public void testForbiddenStatusTransitions() {
        Resource r = new Resource();
        r.setSpaceId("space-123");
        r.setResourceName("Test Room");
        r.setResourceCode("ROOM-1");
        r.setResourceType(ResourceType.ROOM);
        r.setCapacity(4);
        r.setStatus(ResourceStatus.RESERVED);

        OperationResult<Resource> updateRes = resourceService.updateResource("space-123", r, "mock-token").join();
        assertTrue(updateRes.isFailure());
        assertEquals("PERMISSION_DENIED", updateRes.getErrorCode());
    }

    @Test
    public void testSpaceIdImmutability() {
        Resource r = new Resource();
        r.setSpaceId("space-A");
        r.setResourceName("Test Room");
        r.setResourceCode("ROOM-1");
        r.setResourceType(ResourceType.ROOM);
        r.setCapacity(4);

        OperationResult<Resource> updateRes = resourceService.updateResource("space-B", r, "mock-token").join();
        assertTrue(updateRes.isFailure());
        assertEquals("INVALID_ARGUMENT", updateRes.getErrorCode());
    }
}
