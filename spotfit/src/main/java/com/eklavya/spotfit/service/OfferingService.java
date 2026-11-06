package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.*;
import com.eklavya.spotfit.dao.OfferingDao;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service enforcing domain validation, pricing conversion, and compatibility rules for Offerings.
 */
public class OfferingService {

    private final OfferingDao offeringDao;

    public OfferingService() {
        this.offeringDao = new OfferingDao();
    }

    public OfferingService(OfferingDao offeringDao) {
        this.offeringDao = offeringDao;
    }

    public static OperationResult<Long> parsePriceRupeesToPaise(String priceText) {
        if (ValidationUtil.isBlank(priceText)) {
            return OperationResult.failure("PRICE_REQUIRED", "Offering price is required.");
        }
        try {
            BigDecimal rupees = new BigDecimal(priceText.trim());
            if (rupees.scale() > 2) {
                return OperationResult.failure("PRICE_DECIMALS_INVALID", "Price cannot have more than two decimal places.");
            }
            long paise = rupees.movePointRight(2).longValueExact();
            if (paise <= 0) {
                return OperationResult.failure("PRICE_INVALID", "Offering price must be greater than zero.");
            }
            return OperationResult.success(paise);
        } catch (NumberFormatException | ArithmeticException ex) {
            return OperationResult.failure("PRICE_INVALID", "Please enter a valid numeric price amount.");
        }
    }

    public CompletableFuture<OperationResult<Offering>> createOffering(
            String ownerId,
            String businessId,
            String spaceId,
            String offeringName,
            String description,
            String bookingType,
            String pricingType,
            String priceRupeesText,
            String requiredResourceType,
            int defaultDurationMinutes,
            int minimumDurationMinutes,
            int maximumDurationMinutes,
            int bookingIntervalMinutes,
            int capacity,
            int minimumParticipants,
            int maximumParticipants,
            List<String> includedItems,
            List<String> rules,
            String idToken) {

        if (ValidationUtil.isBlank(ownerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }
        if (ValidationUtil.isBlank(businessId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        OperationResult<Long> priceRes = parsePriceRupeesToPaise(priceRupeesText);
        if (priceRes.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(priceRes.getErrorCode(), priceRes.getMessage()));
        }

        OperationResult<Void> validation = validateOfferingInputs(
                offeringName, description, bookingType, pricingType,
                requiredResourceType, defaultDurationMinutes, minimumDurationMinutes,
                maximumDurationMinutes, bookingIntervalMinutes, capacity,
                minimumParticipants, maximumParticipants);

        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        String offeringId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        Offering offering = new Offering(
                offeringId,
                spaceId.trim(),
                businessId.trim(),
                ownerId.trim(),
                ValidationUtil.sanitizeText(offeringName),
                ValidationUtil.sanitizeText(description),
                bookingType.trim(),
                pricingType.trim(),
                priceRes.getData(),
                requiredResourceType.trim(),
                defaultDurationMinutes,
                minimumDurationMinutes,
                maximumDurationMinutes,
                bookingIntervalMinutes,
                capacity,
                minimumParticipants,
                maximumParticipants,
                includedItems != null ? includedItems : new ArrayList<>(),
                rules != null ? rules : new ArrayList<>(),
                OfferingStatus.DRAFT,
                true,
                now,
                now
        );

        return offeringDao.createOffering(spaceId.trim(), offering, idToken);
    }

    public CompletableFuture<OperationResult<Offering>> getOffering(String spaceId, String offeringId, String idToken) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        return offeringDao.getOffering(spaceId.trim(), offeringId, idToken);
    }

    public CompletableFuture<OperationResult<List<Offering>>> getOfferingsForSpace(String spaceId, String ownerId, String idToken) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return offeringDao.getOfferingsForSpace(spaceId.trim(), idToken);
    }

    public CompletableFuture<OperationResult<Offering>> updateOffering(String spaceId, Offering offering, String idToken) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (offering == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Offering object cannot be null."));
        }
        if (ValidationUtil.isBlank(offering.getSpaceId())) {
            offering.setSpaceId(spaceId.trim());
        } else if (!spaceId.trim().equals(offering.getSpaceId().trim())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Cannot move Offering to a different Space."));
        }

        OperationResult<Void> validation = validateOfferingInputs(
                offering.getOfferingName(),
                offering.getDescription(),
                offering.getBookingType(),
                offering.getPricingType(),
                offering.getRequiredResourceType(),
                offering.getDefaultDurationMinutes(),
                offering.getMinimumDurationMinutes(),
                offering.getMaximumDurationMinutes(),
                offering.getBookingIntervalMinutes(),
                offering.getCapacity(),
                offering.getMinimumParticipants(),
                offering.getMaximumParticipants());

        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        if (offering.getPricePaise() <= 0) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("PRICE_INVALID", "Offering price must be greater than zero."));
        }

        if (!OfferingStatus.DRAFT.equalsIgnoreCase(offering.getStatus())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.PERMISSION_DENIED,
                            "Only DRAFT offerings can be edited."));
        }

        offering.setOfferingName(ValidationUtil.sanitizeText(offering.getOfferingName()));
        offering.setDescription(ValidationUtil.sanitizeText(offering.getDescription()));
        offering.setUpdatedAt(System.currentTimeMillis());

        return offeringDao.updateOffering(spaceId.trim(), offering, idToken);
    }

    public CompletableFuture<OperationResult<Offering>> updateOffering(Offering offering, String idToken) {
        if (offering == null || ValidationUtil.isBlank(offering.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Offering and spaceId are required."));
        }
        return updateOffering(offering.getSpaceId(), offering, idToken);
    }

    public OperationResult<Void> validateOfferingInputs(
            String offeringName,
            String description,
            String bookingType,
            String pricingType,
            String requiredResourceType,
            int defaultDurationMinutes,
            int minimumDurationMinutes,
            int maximumDurationMinutes,
            int bookingIntervalMinutes,
            int capacity,
            int minimumParticipants,
            int maximumParticipants) {

        if (ValidationUtil.isBlank(offeringName)) {
            return OperationResult.failure("OFFERING_NAME_REQUIRED", "Offering name is required.");
        }
        if (ValidationUtil.isBlank(description)) {
            return OperationResult.failure("DESCRIPTION_REQUIRED", "Description is required.");
        }
        if (ValidationUtil.isBlank(bookingType) || !BookingType.isValid(bookingType)) {
            return OperationResult.failure("BOOKING_TYPE_INVALID", "Please select a valid booking type.");
        }
        if (ValidationUtil.isBlank(pricingType) || !PricingType.isValid(pricingType)) {
            return OperationResult.failure("PRICING_TYPE_INVALID", "Please select a valid pricing type.");
        }
        if (ValidationUtil.isBlank(requiredResourceType) || !ResourceType.isValid(requiredResourceType)) {
            return OperationResult.failure("REQUIRED_RESOURCE_TYPE_INVALID", "Please select a valid required resource type.");
        }

        OperationResult<Void> compatRes = validateCompatibilityMatrix(bookingType, pricingType, requiredResourceType);
        if (compatRes.isFailure()) {
            return compatRes;
        }

        if (minimumDurationMinutes <= 0 || defaultDurationMinutes <= 0 || maximumDurationMinutes <= 0) {
            return OperationResult.failure("DURATION_INVALID", "Durations must be positive values.");
        }
        if (minimumDurationMinutes > defaultDurationMinutes || defaultDurationMinutes > maximumDurationMinutes) {
            return OperationResult.failure("DURATION_ORDER_INVALID",
                    "Duration constraints must satisfy: Minimum <= Default <= Maximum.");
        }
        if (bookingIntervalMinutes <= 0) {
            return OperationResult.failure("INTERVAL_INVALID", "Booking interval must be greater than zero.");
        }

        if (capacity <= 0) {
            return OperationResult.failure("CAPACITY_INVALID", "Capacity must be greater than zero.");
        }
        if (BookingType.WORKSHOP.equalsIgnoreCase(bookingType)) {
            if (minimumParticipants <= 0 || maximumParticipants <= 0 || minimumParticipants > maximumParticipants) {
                return OperationResult.failure("PARTICIPANTS_INVALID",
                        "Workshop participant limits must be positive and satisfy: Minimum <= Maximum.");
            }
        }

        return OperationResult.success(null, "Offering inputs are valid.");
    }

    public OperationResult<Void> validateCompatibilityMatrix(
            String bookingType,
            String pricingType,
            String requiredResourceType) {

        if (BookingType.SEAT.equalsIgnoreCase(bookingType)) {
            if (!ResourceType.SEAT.equalsIgnoreCase(requiredResourceType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "SEAT booking type requires SEAT resource type.");
            }
            if (!PricingType.PER_PERSON.equalsIgnoreCase(pricingType)
                    && !PricingType.PER_SESSION.equalsIgnoreCase(pricingType)
                    && !PricingType.FLAT.equalsIgnoreCase(pricingType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "SEAT booking type pricing must be PER_PERSON, PER_SESSION, or FLAT.");
            }
        } else if (BookingType.SLOT.equalsIgnoreCase(bookingType)) {
            if (!ResourceType.ROOM.equalsIgnoreCase(requiredResourceType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "SLOT booking type requires ROOM resource type.");
            }
            if (!PricingType.PER_HOUR.equalsIgnoreCase(pricingType)
                    && !PricingType.PER_SESSION.equalsIgnoreCase(pricingType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "SLOT booking type pricing must be PER_HOUR or PER_SESSION.");
            }
        } else if (BookingType.WORKSHOP.equalsIgnoreCase(bookingType)) {
            if (!ResourceType.WORKSHOP_AREA.equalsIgnoreCase(requiredResourceType)
                    && !ResourceType.ROOM.equalsIgnoreCase(requiredResourceType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "WORKSHOP booking type requires WORKSHOP_AREA or ROOM resource type.");
            }
            if (!PricingType.PER_PERSON.equalsIgnoreCase(pricingType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "WORKSHOP booking type pricing must be PER_PERSON.");
            }
        } else if (BookingType.RENTAL.equalsIgnoreCase(bookingType)) {
            if (!ResourceType.EQUIPMENT.equalsIgnoreCase(requiredResourceType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "RENTAL booking type requires EQUIPMENT resource type.");
            }
            if (!PricingType.PER_HOUR.equalsIgnoreCase(pricingType)
                    && !PricingType.PER_DAY.equalsIgnoreCase(pricingType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "RENTAL booking type pricing must be PER_HOUR or PER_DAY.");
            }
        } else if (BookingType.VENUE.equalsIgnoreCase(bookingType)) {
            if (!ResourceType.VENUE_AREA.equalsIgnoreCase(requiredResourceType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "VENUE booking type requires VENUE_AREA resource type.");
            }
            if (!PricingType.PER_HOUR.equalsIgnoreCase(pricingType)
                    && !PricingType.PER_SESSION.equalsIgnoreCase(pricingType)
                    && !PricingType.FLAT.equalsIgnoreCase(pricingType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "VENUE booking type pricing must be PER_HOUR, PER_SESSION, or FLAT.");
            }
        } else if (BookingType.RESOURCE.equalsIgnoreCase(bookingType)) {
            if (!PricingType.PER_HOUR.equalsIgnoreCase(pricingType)
                    && !PricingType.PER_SESSION.equalsIgnoreCase(pricingType)) {
                return OperationResult.failure("COMPATIBILITY_INVALID", "RESOURCE booking type pricing must be PER_HOUR or PER_SESSION.");
            }
        }

        return OperationResult.success(null, "Configuration is compatible.");
    }
}
