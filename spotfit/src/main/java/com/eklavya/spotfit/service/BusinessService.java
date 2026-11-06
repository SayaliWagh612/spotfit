package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.SubmissionReadinessResult;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service class implementing business rules and validations for Business domain objects.
 */
public class BusinessService {

    private final BusinessDao businessDao;

    public BusinessService() {
        this.businessDao = new BusinessDao();
    }

    public BusinessService(BusinessDao businessDao) {
        this.businessDao = businessDao;
    }

    public CompletableFuture<OperationResult<Business>> createBusiness(
            String ownerId,
            String businessName,
            String legalName,
            String businessEmail,
            String businessPhone,
            String description,
            String addressLine,
            String landmark,
            String city,
            String state,
            String postalCode,
            String registrationNumber,
            String idToken) {

        OperationResult<Void> validation = validateBusinessInputs(
                businessName, businessEmail, businessPhone, addressLine, city, state, postalCode);
        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }

        String businessId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        Business business = new Business(
                businessId,
                ownerId.trim(),
                ValidationUtil.sanitizeText(businessName),
                ValidationUtil.sanitizeText(legalName),
                ValidationUtil.normalizeEmail(businessEmail),
                ValidationUtil.sanitizeText(businessPhone),
                ValidationUtil.sanitizeText(description),
                ValidationUtil.sanitizeText(addressLine),
                ValidationUtil.sanitizeText(landmark),
                ValidationUtil.sanitizeText(city),
                ValidationUtil.sanitizeText(state),
                ValidationUtil.sanitizeText(postalCode),
                ValidationUtil.sanitizeText(registrationNumber),
                BusinessStatus.DRAFT,
                true,
                now,
                now
        );

        return businessDao.createBusiness(business, idToken);
    }

    public CompletableFuture<OperationResult<Business>> createV2Business(
            String ownerId,
            String businessName,
            String legalName,
            String businessEmail,
            String businessPhone,
            String description,
            String addressLine,
            String landmark,
            String city,
            String state,
            String postalCode,
            String registrationNumber,
            String category,
            List<String> activityGroups,
            String logoUrl,
            String website,
            String idToken) {

        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }

        String businessId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        Business business = Business.createV2(
                businessId,
                ownerId.trim(),
                ValidationUtil.sanitizeText(businessName),
                ValidationUtil.sanitizeText(legalName),
                ValidationUtil.normalizeEmail(businessEmail),
                ValidationUtil.sanitizeText(businessPhone),
                ValidationUtil.sanitizeText(description),
                ValidationUtil.sanitizeText(addressLine),
                ValidationUtil.sanitizeText(landmark),
                ValidationUtil.sanitizeText(city),
                ValidationUtil.sanitizeText(state),
                ValidationUtil.sanitizeText(postalCode),
                ValidationUtil.sanitizeText(registrationNumber),
                category != null ? category.trim().toUpperCase() : "",
                activityGroups,
                logoUrl != null ? logoUrl.trim() : "",
                website != null ? website.trim() : "",
                BusinessStatus.DRAFT,
                true,
                0L,
                0L,
                "",
                now,
                now
        );

        SubmissionReadinessResult readiness = validateV2Business(business, ownerId.trim());
        if (!readiness.isReady()) {
            String firstError = readiness.getMissingRequirements().isEmpty()
                    ? "Invalid V2 business details."
                    : readiness.getMissingRequirements().get(0);
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, firstError));
        }

        return businessDao.createBusiness(business, idToken);
    }

    public CompletableFuture<OperationResult<Business>> getBusiness(String businessId, String idToken) {
        return businessDao.getBusiness(businessId, idToken);
    }

    public CompletableFuture<OperationResult<List<Business>>> getBusinessesForOwner(String ownerId, String idToken) {
        return businessDao.getBusinessesByOwner(ownerId, idToken);
    }

    public CompletableFuture<OperationResult<Business>> updateBusiness(Business business, String idToken) {
        if (business == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business object cannot be null."));
        }

        OperationResult<Void> validation = validateBusinessInputs(
                business.getBusinessName(),
                business.getBusinessEmail(),
                business.getBusinessPhone(),
                business.getAddressLine(),
                business.getCity(),
                business.getState(),
                business.getPostalCode()
        );
        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        if (!BusinessStatus.DRAFT.equalsIgnoreCase(business.getVerificationStatus())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.PERMISSION_DENIED,
                            "Only DRAFT businesses can be edited."));
        }

        business.setBusinessName(ValidationUtil.sanitizeText(business.getBusinessName()));
        business.setLegalName(ValidationUtil.sanitizeText(business.getLegalName()));
        business.setBusinessEmail(ValidationUtil.normalizeEmail(business.getBusinessEmail()));
        business.setBusinessPhone(ValidationUtil.sanitizeText(business.getBusinessPhone()));
        business.setDescription(ValidationUtil.sanitizeText(business.getDescription()));
        business.setAddressLine(ValidationUtil.sanitizeText(business.getAddressLine()));
        business.setLandmark(ValidationUtil.sanitizeText(business.getLandmark()));
        business.setCity(ValidationUtil.sanitizeText(business.getCity()));
        business.setState(ValidationUtil.sanitizeText(business.getState()));
        business.setPostalCode(ValidationUtil.sanitizeText(business.getPostalCode()));
        business.setRegistrationNumber(ValidationUtil.sanitizeText(business.getRegistrationNumber()));

        return businessDao.updateBusiness(business, idToken);
    }

    public CompletableFuture<OperationResult<Business>> updateV2Business(Business business, String idToken) {
        if (business == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business object cannot be null."));
        }

        SubmissionReadinessResult readiness = validateV2Business(business, business.getOwnerId());
        if (!readiness.isReady()) {
            String firstError = readiness.getMissingRequirements().isEmpty()
                    ? "Invalid V2 business details."
                    : readiness.getMissingRequirements().get(0);
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, firstError));
        }

        business.setBusinessName(ValidationUtil.sanitizeText(business.getBusinessName()));
        business.setLegalName(ValidationUtil.sanitizeText(business.getLegalName()));
        business.setBusinessEmail(ValidationUtil.normalizeEmail(business.getBusinessEmail()));
        business.setBusinessPhone(ValidationUtil.sanitizeText(business.getBusinessPhone()));
        business.setDescription(ValidationUtil.sanitizeText(business.getDescription()));
        business.setAddressLine(ValidationUtil.sanitizeText(business.getAddressLine()));
        business.setLandmark(ValidationUtil.sanitizeText(business.getLandmark()));
        business.setCity(ValidationUtil.sanitizeText(business.getCity()));
        business.setState(ValidationUtil.sanitizeText(business.getState()));
        business.setPostalCode(ValidationUtil.sanitizeText(business.getPostalCode()));
        business.setRegistrationNumber(ValidationUtil.sanitizeText(business.getRegistrationNumber()));
        if (business.getWebsite() != null) {
            business.setWebsite(business.getWebsite().trim());
        }
        business.setLogoUrl("");

        return businessDao.updateBusiness(business, idToken);
    }

    public SubmissionReadinessResult validateV2Business(Business business, String authenticatedOwnerId) {
        SubmissionReadinessResult result = new SubmissionReadinessResult();
        List<String> errors = result.getMissingRequirements();

        if (business == null) {
            errors.add("Business object cannot be null.");
            result.setReady(false);
            return result;
        }

        if (business.getSchemaVersion() != 2) {
            errors.add("Business schema version must be 2.");
        }

        if (authenticatedOwnerId == null || authenticatedOwnerId.isBlank()) {
            errors.add("Authenticated Owner ID is required.");
        } else if (!authenticatedOwnerId.trim().equals(business.getOwnerId())) {
            errors.add("Business owner ID does not match authenticated owner.");
        }

        if (!business.isActive()) {
            errors.add("Business is not active.");
        }

        OperationResult<Void> baseValidation = validateBusinessInputs(
                business.getBusinessName(),
                business.getBusinessEmail(),
                business.getBusinessPhone(),
                business.getAddressLine(),
                business.getCity(),
                business.getState(),
                business.getPostalCode()
        );
        if (baseValidation.isFailure()) {
            errors.add(baseValidation.getMessage());
        }

        String category = business.getCategory();
        if (category == null || category.isBlank() || !SpaceCategory.isValid(category)) {
            errors.add("Invalid business category.");
        } else if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(category)) {
            errors.add("Owner V2 currently supports the Creativity category only.");
        }

        List<String> groups = business.getActivityGroups();
        if (groups == null || groups.isEmpty()) {
            errors.add("Activity groups are required.");
        } else {
            Set<String> seen = new HashSet<>();
            for (String grp : groups) {
                if (grp == null || grp.isBlank()) {
                    errors.add("Activity group cannot be blank.");
                } else {
                    String trimmed = grp.trim().toUpperCase();
                    if (!seen.add(trimmed)) {
                        errors.add("Duplicate activity group: " + grp);
                    }
                    if (SpaceCategory.CREATIVITY.equalsIgnoreCase(category)) {
                        if (!CreativityActivityGroup.isValid(trimmed)) {
                            errors.add("Invalid activity group for CREATIVITY category: " + grp);
                        }
                    }
                }
            }
        }

        String website = business.getWebsite();
        if (website != null && !website.isBlank()) {
            String lower = website.trim().toLowerCase();
            if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
                errors.add("Website URL must start with http:// or https://");
            }
        }

        String logoUrl = business.getLogoUrl();
        if (logoUrl != null && !logoUrl.isBlank()) {
            String lower = logoUrl.trim().toLowerCase();
            if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
                errors.add("Logo URL must start with http:// or https://");
            }
        }

        String status = business.getVerificationStatus();
        if (BusinessStatus.APPROVED.equalsIgnoreCase(status)
                || BusinessStatus.UNDER_REVIEW.equalsIgnoreCase(status)
                || BusinessStatus.SUSPENDED.equalsIgnoreCase(status)) {
            errors.add("Owner cannot edit or submit business in status: " + status);
        }

        result.setReady(errors.isEmpty());
        return result;
    }

    public OperationResult<Void> validateBusinessInputs(
            String businessName,
            String businessEmail,
            String businessPhone,
            String addressLine,
            String city,
            String state,
            String postalCode) {

        if (ValidationUtil.isBlank(businessName)) {
            return OperationResult.failure("BUSINESS_NAME_REQUIRED", "Business name is required.");
        }
        OperationResult<Void> emailRes = ValidationUtil.validateEmail(businessEmail);
        if (emailRes.isFailure()) {
            return emailRes;
        }
        if (ValidationUtil.isBlank(businessPhone)) {
            return OperationResult.failure("BUSINESS_PHONE_REQUIRED", "Business phone is required.");
        }
        if (!ValidationUtil.isValidPhone(businessPhone)) {
            return OperationResult.failure("BUSINESS_PHONE_INVALID", "Please enter a valid phone number.");
        }
        if (ValidationUtil.isBlank(addressLine)) {
            return OperationResult.failure("ADDRESS_LINE_REQUIRED", "Address line is required.");
        }
        if (ValidationUtil.isBlank(city)) {
            return OperationResult.failure("CITY_REQUIRED", "City is required.");
        }
        if (ValidationUtil.isBlank(state)) {
            return OperationResult.failure("STATE_REQUIRED", "State is required.");
        }
        if (ValidationUtil.isBlank(postalCode)) {
            return OperationResult.failure("POSTAL_CODE_REQUIRED", "Postal code is required.");
        }
        return OperationResult.success(null, "Business inputs are valid.");
    }

    public boolean isEligibleForV2SpaceCreation(Business business, String authenticatedOwnerId) {
        if (business == null) {
            return false;
        }
        if (business.getSchemaVersion() != 2) {
            return false;
        }
        if (!business.isActive()) {
            return false;
        }
        if (authenticatedOwnerId != null && !authenticatedOwnerId.isBlank()
                && !authenticatedOwnerId.trim().equals(business.getOwnerId())) {
            return false;
        }
        String status = business.getVerificationStatus() != null ? business.getVerificationStatus().trim() : "";
        if (!BusinessStatus.APPROVED.equalsIgnoreCase(status)) {
            return false;
        }
        String category = business.getCategory() != null ? business.getCategory().trim() : "";
        if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(category)) {
            return false;
        }
        return true;
    }

    public List<String> getV2SpaceEligibilityFailures(Business business, String authenticatedOwnerId) {
        List<String> reasons = new ArrayList<>();
        if (business == null) {
            reasons.add("Business object is null.");
            return reasons;
        }
        if (business.getSchemaVersion() != 2) {
            reasons.add("Schema version V" + business.getSchemaVersion() + " (V2 required).");
        }
        if (!business.isActive()) {
            reasons.add("Business is inactive.");
        }
        if (authenticatedOwnerId != null && !authenticatedOwnerId.isBlank()
                && !authenticatedOwnerId.trim().equals(business.getOwnerId())) {
            reasons.add("Business owner mismatch.");
        }
        String status = business.getVerificationStatus() != null ? business.getVerificationStatus().trim() : "";
        if (!BusinessStatus.APPROVED.equalsIgnoreCase(status)) {
            reasons.add("Verification status is " + (status.isEmpty() ? "UNKNOWN" : status) + " (APPROVED required).");
        }
        String category = business.getCategory() != null ? business.getCategory().trim() : "";
        if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(category)) {
            reasons.add("Category is " + (category.isEmpty() ? "UNSPECIFIED" : category) + " (CREATIVITY required).");
        }
        return reasons;
    }
}
