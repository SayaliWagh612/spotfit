package com.eklavya.spotfit.rules;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class Phase8AFirestoreRulesTest {

    private static String rulesContent;

    @BeforeAll
    public static void loadRules() throws Exception {
        File rulesFile = new File("firestore.rules");
        assertTrue(rulesFile.exists(), "firestore.rules must exist in working directory");
        rulesContent = Files.readString(Path.of("firestore.rules"));
        assertNotNull(rulesContent);
        assertFalse(rulesContent.isBlank());
    }

    @Test
    @DisplayName("Verify Seats subcollection match block exists")
    public void testSeatsMatchBlockExists() {
        assertTrue(rulesContent.contains("match /spaces/{spaceId}/seats/{seatId}"), "Must contain match rule for seats subcollection");
        assertTrue(rulesContent.contains("parentSpace().data.bookingType == \"SEAT\""), "Must check SEAT bookingType");
        assertTrue(rulesContent.contains("request.resource.data.status == \"AVAILABLE\""), "Must require AVAILABLE initial status");
    }

    @Test
    @DisplayName("Verify Sessions subcollection match block exists")
    public void testSessionsMatchBlockExists() {
        assertTrue(rulesContent.contains("match /spaces/{spaceId}/sessions/{sessionId}"), "Must contain match rule for sessions subcollection");
        assertTrue(rulesContent.contains("parentSpace().data.bookingType == \"WORKSHOP\""), "Must check WORKSHOP bookingType");
        assertTrue(rulesContent.contains("request.resource.data.status == \"SCHEDULED\""), "Must require SCHEDULED initial status");
        assertTrue(rulesContent.contains("request.resource.data.bookedCount == 0"), "Must require initial bookedCount == 0");
    }

    @Test
    @DisplayName("Verify write operations denied for SUBMITTED/UNDER_REVIEW states")
    public void testWriteOperationsDeniedWhenSubmittedOrReview() {
        assertTrue(rulesContent.contains("parentSpace().data.approvalStatus in [\"DRAFT\", \"REJECTED\", \"APPROVED\"]"));
    }
}
