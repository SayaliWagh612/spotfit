package com.eklavya.spotfit.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessFirestoreRulesV2Phase5Test {

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
    @DisplayName("Verify /businesses/{businessId} match block exists")
    public void testBusinessMatchBlockExists() {
        assertTrue(rulesContent.contains("match /businesses/{businessId}"), "Must contain match rule for businesses collection");
    }

    @Test
    @DisplayName("Verify V1 owner create and update rules preserved")
    public void testV1RulesPreserved() {
        assertTrue(rulesContent.contains("!('schemaVersion' in request.resource.data) || request.resource.data.schemaVersion == 1"),
                "Must preserve V1 backward compatibility for schemaVersion 1 or omitted version");
    }

    @Test
    @DisplayName("Verify V2 create enforces schemaVersion 2, businessId match, CREATIVITY category, and active status")
    public void testV2CreateRules() {
        assertTrue(rulesContent.contains("request.resource.data.schemaVersion == 2"), "Must enforce schemaVersion 2");
        assertTrue(rulesContent.contains("request.resource.data.businessId == businessId"), "Must enforce document ID equals businessId");
        assertTrue(rulesContent.contains("request.resource.data.category == \"CREATIVITY\""), "Must enforce CREATIVITY category");
        assertTrue(rulesContent.contains("request.resource.data.activityGroups is list"), "Must enforce activityGroups list type");
        assertTrue(rulesContent.contains("request.resource.data.activityGroups.size() > 0"), "Must enforce non-empty activity groups");
        assertTrue(rulesContent.contains("request.resource.data.submittedAt == 0"), "Must require initial submittedAt to be 0");
        assertTrue(rulesContent.contains("request.resource.data.reviewedAt == 0"), "Must require initial reviewedAt to be 0");
        assertTrue(rulesContent.contains("request.resource.data.rejectionReason == \"\""), "Must require initial rejectionReason to be empty");
        assertTrue(rulesContent.contains("request.resource.data.logoUrl == \"\""), "Must require logoUrl to be empty in V2 creation");
    }

    @Test
    @DisplayName("Verify owner identity spoofing protection and system immutable field protection")
    public void testOwnerIdentityProtection() {
        assertTrue(rulesContent.contains("request.resource.data.ownerId == request.auth.uid"), "Must enforce owner identity match on create");
        assertTrue(rulesContent.contains("resource.data.ownerId == request.auth.uid"), "Must enforce owner identity match on read/update");
        assertTrue(rulesContent.contains("request.resource.data.ownerId == resource.data.ownerId"), "Must protect ownerId against modification");
        assertTrue(rulesContent.contains("request.resource.data.businessId == resource.data.businessId"), "Must protect businessId against modification");
        assertTrue(rulesContent.contains("request.resource.data.active == resource.data.active"), "Must protect active status against modification");
        assertTrue(rulesContent.contains("request.resource.data.createdAt == resource.data.createdAt"), "Must protect createdAt timestamp against modification");
    }

    @Test
    @DisplayName("Verify status transition rules for DRAFT edit, REJECTED reset to DRAFT, and SUBMITTED transition")
    public void testStatusTransitionRules() {
        assertTrue(rulesContent.contains("resource.data.verificationStatus == \"DRAFT\" && request.resource.data.verificationStatus == \"DRAFT\""),
                "Must allow edit while in DRAFT");
        assertTrue(rulesContent.contains("resource.data.verificationStatus == \"REJECTED\" && request.resource.data.verificationStatus == \"DRAFT\""),
                "Must allow reset from REJECTED to DRAFT on edit");
        assertTrue(rulesContent.contains("request.resource.data.verificationStatus == \"SUBMITTED\""),
                "Must allow transition to SUBMITTED");
    }

    @Test
    @DisplayName("Verify customer read access rule for APPROVED active businesses")
    public void testCustomerReadAccessRule() {
        assertTrue(rulesContent.contains("isActiveCustomer() && resource.data.verificationStatus == \"APPROVED\" && resource.data.active == true"),
                "Must preserve Customer read access rule for active APPROVED businesses");
    }
}
