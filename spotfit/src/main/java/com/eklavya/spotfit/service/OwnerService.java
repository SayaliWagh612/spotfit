package com.eklavya.spotfit.service;

import com.eklavya.spotfit.dao.OwnerDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Owner;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.concurrent.CompletableFuture;

/**
 * Service class for Owner profile business logic and validation.
 */
public class OwnerService {

    private final OwnerDao ownerDao;

    public OwnerService() {
        this(new OwnerDao());
    }

    public OwnerService(OwnerDao ownerDao) {
        this.ownerDao = (ownerDao != null) ? ownerDao : new OwnerDao();
    }

    public CompletableFuture<OperationResult<Owner>> createInitialProfile(Owner owner, String idToken) {
        if (owner == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_OWNER", "Owner profile data cannot be null."));
        }

        String name = ValidationUtil.sanitizeText(owner.getName());
        String email = ValidationUtil.normalizeEmail(owner.getEmail());
        String phone = ValidationUtil.sanitizeText(owner.getPhone());

        if (name == null || name.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_NAME", "Owner full name is required."));
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_EMAIL", "A valid email address is required."));
        }
        if (phone != null && !phone.isBlank() && !ValidationUtil.isValidPhone(phone)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_PHONE", "Please enter a valid 10-digit phone number."));
        }

        owner.setName(name);
        owner.setEmail(email);
        owner.setPhone(phone != null ? phone : "");

        return ownerDao.createOwner(owner, idToken);
    }

    public CompletableFuture<OperationResult<Owner>> initializeOwnerProfile(
            String uid,
            String name,
            String email,
            String phone,
            boolean emailVerified,
            String idToken) {

        if (uid == null || uid.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        String cleanName = ValidationUtil.sanitizeText(name);
        String cleanEmail = ValidationUtil.normalizeEmail(email);
        String cleanPhone = ValidationUtil.sanitizeText(phone);

        long now = System.currentTimeMillis();
        Owner owner = new Owner(
                uid.trim(),
                cleanName != null ? cleanName : "",
                cleanEmail != null ? cleanEmail : "",
                cleanPhone != null ? cleanPhone : "",
                com.eklavya.spotfit.constants.UserRole.OWNER,
                "",
                "",
                emailVerified,
                false,
                true,
                now,
                now
        );

        return createInitialProfile(owner, idToken);
    }

    public CompletableFuture<OperationResult<Owner>> getOwner(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        return ownerDao.getOwner(ownerId, idToken);
    }

    public CompletableFuture<OperationResult<Owner>> updateBasicProfile(
            String ownerId,
            String name,
            String phone,
            String city,
            String profileImageUrl,
            String idToken) {

        String cleanName = ValidationUtil.sanitizeText(name);
        String cleanPhone = ValidationUtil.sanitizeText(phone);
        String cleanCity = ValidationUtil.sanitizeText(city);
        String cleanImage = ValidationUtil.sanitizeText(profileImageUrl);

        if (cleanName == null || cleanName.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_NAME", "Full name cannot be blank."));
        }
        if (cleanPhone != null && !cleanPhone.isBlank() && !ValidationUtil.isValidPhone(cleanPhone)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_PHONE", "Please enter a valid 10-digit phone number."));
        }

        return ownerDao.updateBasicProfile(ownerId, cleanName, cleanPhone, cleanCity, cleanImage, idToken);
    }

    public CompletableFuture<OperationResult<Owner>> markEmailVerified(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        return ownerDao.updateEmailVerification(ownerId, true, idToken);
    }

    public CompletableFuture<OperationResult<Owner>> completeProfile(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        return ownerDao.completeProfile(ownerId, idToken);
    }
}
