package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.InvitationStatus;
import com.eklavya.spotfit.constants.ManagerPermission;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model representing a Manager Invitation created by an Owner.
 * The invitation code serves as the Firestore document ID under /managerInvitations/{invitationCode}.
 */
public class ManagerInvitation {

    private static final String CODE_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"; // Readable uppercase alphanumeric without ambiguous chars
    private static final SecureRandom RANDOM = new SecureRandom();
    public static final long DEFAULT_EXPIRY_MILLIS = 7 * 24 * 60 * 60 * 1000L; // 7 days

    private String invitationId;
    private String invitationCode;

    private String ownerId;
    private String managerEmail;

    private List<String> assignedSpaceIds = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    private String status;
    private boolean active = true;
    private long expiresAt;

    private String acceptedByManagerId;
    private long acceptedAt;

    private long createdAt;
    private long updatedAt;

    public ManagerInvitation() {
        this.invitationId = UUID.randomUUID().toString();
        this.status = InvitationStatus.PENDING;
        this.active = true;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.expiresAt = now + DEFAULT_EXPIRY_MILLIS;
        this.permissions = ManagerPermission.getDefaultPermissions();
    }

    public ManagerInvitation(String ownerId, String managerEmail, List<String> assignedSpaceIds, List<String> permissions) {
        this();
        this.invitationCode = generateSecureInvitationCode();
        this.ownerId = ownerId;
        this.managerEmail = managerEmail != null ? managerEmail.trim().toLowerCase() : "";
        if (assignedSpaceIds != null) {
            this.assignedSpaceIds = new ArrayList<>(assignedSpaceIds);
        }
        if (permissions != null && !permissions.isEmpty()) {
            this.permissions = new ArrayList<>(permissions);
        }
    }

    public static ManagerInvitation createDefault(String ownerId, String managerEmail, List<String> assignedSpaceIds, List<String> permissions) {
        return new ManagerInvitation(ownerId, managerEmail, assignedSpaceIds, permissions);
    }

    public ManagerInvitation(String invitationId, String invitationCode, String ownerId, String managerEmail,
                             List<String> assignedSpaceIds, List<String> permissions, String status,
                             long expiresAt, String acceptedByManagerId, long acceptedAt,
                             long createdAt, long updatedAt) {
        this(invitationId, invitationCode, ownerId, managerEmail, assignedSpaceIds, permissions, status, true, expiresAt, acceptedByManagerId, acceptedAt, createdAt, updatedAt);
    }

    public ManagerInvitation(String invitationId, String invitationCode, String ownerId, String managerEmail,
                             List<String> assignedSpaceIds, List<String> permissions, String status, boolean active,
                             long expiresAt, String acceptedByManagerId, long acceptedAt,
                             long createdAt, long updatedAt) {
        this.invitationId = invitationId;
        this.invitationCode = invitationCode != null ? invitationCode.trim().toUpperCase() : "";
        this.ownerId = ownerId;
        this.managerEmail = managerEmail != null ? managerEmail.trim().toLowerCase() : "";
        this.assignedSpaceIds = assignedSpaceIds != null ? new ArrayList<>(assignedSpaceIds) : new ArrayList<>();
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
        this.status = status;
        this.active = active;
        this.expiresAt = expiresAt;
        this.acceptedByManagerId = acceptedByManagerId;
        this.acceptedAt = acceptedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Generates a cryptographically random, readable 16-character uppercase invitation code with >80 bits of entropy.
     */
    public static String generateSecureInvitationCode() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append("-");
            }
            int index = RANDOM.nextInt(CODE_ALPHABET.length());
            sb.append(CODE_ALPHABET.charAt(index));
        }
        return sb.toString().toUpperCase();
    }

    public static String normalizeCode(String rawCode) {
        if (rawCode == null) {
            return "";
        }
        return rawCode.trim().toUpperCase().replaceAll("\\s+", "");
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode != null ? invitationCode.trim().toUpperCase() : "";
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail != null ? managerEmail.trim().toLowerCase() : "";
    }

    public List<String> getAssignedSpaceIds() {
        return assignedSpaceIds;
    }

    public void setAssignedSpaceIds(List<String> assignedSpaceIds) {
        this.assignedSpaceIds = assignedSpaceIds != null ? new ArrayList<>(assignedSpaceIds) : new ArrayList<>();
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getAcceptedByManagerId() {
        return acceptedByManagerId;
    }

    public String getAcceptedByUserId() {
        return acceptedByManagerId != null ? acceptedByManagerId : "";
    }

    public void setAcceptedByManagerId(String acceptedByManagerId) {
        this.acceptedByManagerId = acceptedByManagerId;
    }

    public long getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(long acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isPending() {
        return InvitationStatus.PENDING.equals(status) && !isExpired();
    }
}
