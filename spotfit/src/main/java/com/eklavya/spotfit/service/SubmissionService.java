package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.OfferingStatus;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for assessing Business and Space readiness and enforcing owner
 * submission rules.
 */
public class SubmissionService {

    /**
     * Evaluates Business submission readiness.
     */
    public OperationResult<SubmissionReadinessResult> validateBusinessReadiness(Business business, String ownerId) {
        List<String> missing = new ArrayList<>();

        if (business == null) {
            return OperationResult.failure("BUSINESS_NOT_FOUND", "Business does not exist.");
        }

        if (ValidationUtil.isBlank(ownerId) || !ownerId.equals(business.getOwnerId())) {
            return OperationResult.failure("PERMISSION_DENIED", "You do not own this Business.");
        }

        if (!business.isActive()) {
            missing.add("Business is inactive.");
        }

        String status = business.getVerificationStatus() == null
                ? BusinessStatus.DRAFT
                : business.getVerificationStatus().trim().toUpperCase();
        if (BusinessStatus.SUBMITTED.equalsIgnoreCase(status)) {
            missing.add("Business has already been submitted and is pending review.");
        } else if (BusinessStatus.APPROVED.equalsIgnoreCase(status)) {
            missing.add("Business is already approved.");
        } else if (!BusinessStatus.DRAFT.equalsIgnoreCase(status)
                && !BusinessStatus.REJECTED.equalsIgnoreCase(status)) {
            missing.add("Business is in an invalid status for submission: " + status);
        }

        if (ValidationUtil.isBlank(business.getBusinessName())) {
            missing.add("Business name is required.");
        }

        if (ValidationUtil.isBlank(business.getBusinessEmail()) || !business.getBusinessEmail().contains("@")) {
            missing.add("Valid business email address is required.");
        }

        if (ValidationUtil.isBlank(business.getBusinessPhone())
                || business.getBusinessPhone().replaceAll("\\D", "").length() < 10) {
            missing.add("Valid 10-digit business phone number is required.");
        }

        if (ValidationUtil.isBlank(business.getAddressLine())) {
            missing.add("Address line is required.");
        }

        if (ValidationUtil.isBlank(business.getCity())) {
            missing.add("City is required.");
        }

        if (ValidationUtil.isBlank(business.getState())) {
            missing.add("State is required.");
        }

        if (ValidationUtil.isBlank(business.getPostalCode()) || !business.getPostalCode().matches("\\d{6}")) {
            missing.add("Valid 6-digit postal code is required.");
        }

        boolean ready = missing.isEmpty();
        return OperationResult.success(new SubmissionReadinessResult(ready, missing));
    }

    /**
     * Evaluates Space submission readiness including parent Business approval,
     * required profile fields,
     * active Resources, DRAFT Offerings, and Offering-compatible Resource
     * availability schedules.
     */
    public OperationResult<SubmissionReadinessResult> validateSpaceReadiness(
            Business parentBusiness,
            Space space,
            List<Resource> resources,
            List<Offering> offerings,
            Map<String, List<AvailabilityRule>> resourceRuleMap,
            String ownerId) {

        List<String> missing = new ArrayList<>();

        // 1. Parent Business Validation
        if (parentBusiness == null) {
            missing.add("Parent Business does not exist.");
        } else {
            if (!ownerId.equals(parentBusiness.getOwnerId())) {
                missing.add("Parent Business does not belong to authenticated Owner.");
            }
            if (!parentBusiness.isActive()) {
                missing.add("Parent Business is inactive.");
            }
            if (!BusinessStatus.APPROVED.equalsIgnoreCase(parentBusiness.getVerificationStatus())) {
                missing.add("Parent Business must be APPROVED before submitting Space for review.");
            }
        }

        // 2. Space Validation
        if (space == null) {
            return OperationResult.failure("SPACE_NOT_FOUND", "Space does not exist.");
        }

        if (ValidationUtil.isBlank(ownerId) || !ownerId.equals(space.getOwnerId())) {
            return OperationResult.failure("PERMISSION_DENIED", "You do not own this Space.");
        }

        if (parentBusiness != null && !parentBusiness.getBusinessId().equals(space.getBusinessId())) {
            missing.add("Space does not belong to the specified parent Business.");
        }

        String spaceStatus = space.getStatus();
        if (SpaceStatus.SUBMITTED.equalsIgnoreCase(spaceStatus)) {
            missing.add("Space has already been submitted and is pending review.");
        } else if (SpaceStatus.LIVE.equalsIgnoreCase(spaceStatus)) {
            missing.add("Space is already live.");
        } else if (!SpaceStatus.DRAFT.equalsIgnoreCase(spaceStatus)
                && !SpaceStatus.REJECTED.equalsIgnoreCase(spaceStatus)) {
            missing.add("Space status is invalid for submission: " + spaceStatus);
        }

        if (ValidationUtil.isBlank(space.getSpaceName())) {
            missing.add("Space name is required.");
        }
        if (ValidationUtil.isBlank(space.getCategory())) {
            missing.add("Category is required.");
        }
        if (ValidationUtil.isBlank(space.getSpaceType())) {
            missing.add("Space type is required.");
        }
        if (ValidationUtil.isBlank(space.getAddressLine())) {
            missing.add("Address line is required.");
        }
        if (ValidationUtil.isBlank(space.getCity())) {
            missing.add("City is required.");
        }
        if (ValidationUtil.isBlank(space.getState())) {
            missing.add("State is required.");
        }
        if (ValidationUtil.isBlank(space.getPostalCode()) || !space.getPostalCode().matches("\\d{6}")) {
            missing.add("Valid 6-digit postal code is required.");
        }
        if (space.getCapacity() <= 0) {
            missing.add("Space capacity must be greater than zero.");
        }
        if (space.getOperatingDays() == null || space.getOperatingDays().isEmpty()) {
            missing.add("Space operating days must be configured.");
        }
        if (ValidationUtil.isBlank(space.getOpeningTime()) || ValidationUtil.isBlank(space.getClosingTime())) {
            missing.add("Space opening and closing times must be configured.");
        }
        if (ValidationUtil.isBlank(space.getTimeZoneId())) {
            missing.add("Space timezone must be configured.");
        }

        // 3. Active Resources Validation
        List<Resource> activeResources = new ArrayList<>();
        if (resources != null) {
            for (Resource r : resources) {
                if (r != null && r.isActive()) {
                    activeResources.add(r);
                }
            }
        }

        if (activeResources.isEmpty()) {
            missing.add("Space must have at least one active Resource.");
        } else {
            for (Resource r : activeResources) {
                if (ValidationUtil.isBlank(r.getResourceType())) {
                    missing.add("Resource '" + r.getResourceName() + "' must have a valid resource type.");
                }
                if (r.getCapacity() <= 0) {
                    missing.add("Resource '" + r.getResourceName() + "' capacity must be greater than zero.");
                }
                if (ValidationUtil.isBlank(r.getStatus())) {
                    missing.add("Resource '" + r.getResourceName() + "' operational status must be set.");
                }
            }
        }

        // 4. Offerings Validation
        List<Offering> activeOfferings = new ArrayList<>();
        if (offerings != null) {
            for (Offering o : offerings) {
                if (o != null && o.isActive()) {
                    activeOfferings.add(o);
                }
            }
        }

        if (activeOfferings.isEmpty()) {
            missing.add("Space must have at least one Offering.");
        } else {
            for (Offering o : activeOfferings) {
                if (ValidationUtil.isBlank(o.getBookingType())) {
                    missing.add("Offering '" + o.getOfferingName() + "' must have a valid booking type.");
                }
                if (ValidationUtil.isBlank(o.getPricingType())) {
                    missing.add("Offering '" + o.getOfferingName() + "' must have a valid pricing type.");
                }
                if (o.getPricePaise() <= 0) {
                    missing.add("Offering '" + o.getOfferingName() + "' price must be greater than zero.");
                }

                // 5. Resource-Specific Availability Schedule Validation
                // Confirm that a compatible active Resource exists AND that specific Resource
                // has at least one active Availability Rule
                boolean compatibleResourceWithScheduleFound = false;
                for (Resource res : activeResources) {
                    boolean isCompatible = ValidationUtil.isBlank(o.getRequiredResourceType())
                            || o.getRequiredResourceType().equalsIgnoreCase(res.getResourceType());

                    if (isCompatible) {
                        List<AvailabilityRule> rules = resourceRuleMap != null
                                ? resourceRuleMap.get(res.getResourceId())
                                : null;
                        boolean hasActiveRule = false;
                        if (rules != null) {
                            for (AvailabilityRule r : rules) {
                                if (r != null && r.isActive()) {
                                    hasActiveRule = true;
                                    break;
                                }
                            }
                        }
                        if (hasActiveRule) {
                            compatibleResourceWithScheduleFound = true;
                            break;
                        }
                    }
                }

                if (!compatibleResourceWithScheduleFound) {
                    missing.add("Offering '" + o.getOfferingName()
                            + "' requires a compatible active Resource with at least one active Availability Rule.");
                }
            }
        }

        boolean ready = missing.isEmpty();
        return OperationResult.success(new SubmissionReadinessResult(ready, missing));
    }

    /**
     * Evaluates V2 Space submission readiness using authoritative parent Business,
     * Space, and child entities.
     */
    public OperationResult<SubmissionReadinessResult> validateV2SpaceReadiness(
            Business parentBusiness,
            Space space,
            List<Seat> seats,
            List<AvailabilityRule> spaceAvailabilityRules,
            List<WorkshopSession> sessions,
            String ownerId) {

        List<String> missing = new ArrayList<>();

        if (space == null) {
            return OperationResult.failure("SPACE_NOT_FOUND", "Space does not exist.");
        }

        if (ValidationUtil.isBlank(ownerId) || !ownerId.equals(space.getOwnerId())) {
            return OperationResult.failure("PERMISSION_DENIED", "You do not own this Space.");
        }

        if (space.getSchemaVersion() != 2) {
            missing.add("Space schemaVersion must be 2.");
        }

        if (!space.isActive()) {
            missing.add("Space is inactive.");
        }

        String approvalStatus = space.getApprovalStatus();
        if (com.eklavya.spotfit.constants.SpaceApprovalStatus.SUBMITTED.equalsIgnoreCase(approvalStatus)) {
            missing.add("Space has already been submitted and is pending developer review.");
        } else if (com.eklavya.spotfit.constants.SpaceApprovalStatus.UNDER_REVIEW.equalsIgnoreCase(approvalStatus)) {
            missing.add("Space is currently under developer review.");
        } else if (!com.eklavya.spotfit.constants.SpaceApprovalStatus.DRAFT.equalsIgnoreCase(approvalStatus)
                && !com.eklavya.spotfit.constants.SpaceApprovalStatus.REJECTED.equalsIgnoreCase(approvalStatus)
                && !com.eklavya.spotfit.constants.SpaceApprovalStatus.APPROVED.equalsIgnoreCase(approvalStatus)) {
            missing.add("Space approval status is invalid: " + approvalStatus);
        }

        // Parent Business checks
        if (parentBusiness == null) {
            missing.add("Parent Business does not exist.");
        } else {
            if (parentBusiness.getSchemaVersion() != 2) {
                missing.add("Parent Business schemaVersion must be 2.");
            }
            if (!ownerId.equals(parentBusiness.getOwnerId())) {
                missing.add("Parent Business does not belong to authenticated Owner.");
            }
            if (!parentBusiness.isActive()) {
                missing.add("Parent Business is inactive.");
            }
            if (!BusinessStatus.APPROVED.equalsIgnoreCase(parentBusiness.getVerificationStatus())) {
                missing.add("Parent Business must be APPROVED before submitting Space for review.");
            }
            if (!com.eklavya.spotfit.constants.SpaceCategory.CREATIVITY
                    .equalsIgnoreCase(parentBusiness.getCategory())) {
                missing.add("Parent Business category must be CREATIVITY.");
            }
            if (!parentBusiness.getBusinessId().equals(space.getBusinessId())) {
                missing.add("Space does not belong to the specified parent Business.");
            }
            if (space.getActivityGroup() != null && !space.getActivityGroup().isBlank()) {
                if (parentBusiness.getActivityGroups() == null
                        || !parentBusiness.getActivityGroups().contains(space.getActivityGroup())) {
                    missing.add("Parent Business does not support activity group: " + space.getActivityGroup());
                }
            }
        }

        // Profile / Location checks
        if (ValidationUtil.isBlank(space.getSpaceName())) {
            missing.add("Space name is required.");
        }
        if (ValidationUtil.isBlank(space.getCategory())) {
            missing.add("Category is required.");
        }
        if (ValidationUtil.isBlank(space.getActivityGroup())) {
            missing.add("Activity group is required.");
        }
        if (ValidationUtil.isBlank(space.getActivityType())) {
            missing.add("Activity type is required.");
        }

        SpaceLocation loc = space.getSpaceLocation();
        if (loc == null || ValidationUtil.isBlank(loc.getAddressLine())) {
            missing.add("Address line is required.");
        }
        if (loc == null || ValidationUtil.isBlank(loc.getCity())) {
            missing.add("City is required.");
        }
        if (loc == null || ValidationUtil.isBlank(loc.getState())) {
            missing.add("State is required.");
        }
        if (loc == null || ValidationUtil.isBlank(loc.getPostalCode()) || !loc.getPostalCode().matches("\\d{6}")) {
            missing.add("Valid 6-digit postal code is required.");
        }

        if (space.getCapacity() <= 0) {
            missing.add("Space capacity must be greater than zero.");
        }

        if (ValidationUtil.isBlank(space.getTimeZoneId())) {
            missing.add("Space timezone must be configured.");
        }

        if (!com.eklavya.spotfit.constants.PricingUnit.isValid(space.getPricingUnit())) {
            missing.add("Valid pricing unit is required.");
        }

        if (ValidationUtil.isBlank(space.getBookingType()) || !space.hasMatchingBookingConfig()) {
            missing.add("Valid booking type and matching configuration are required.");
        }

        // Operating hours: at least 1 active day
        Map<String, OperatingDayConfig> oh = space.getOperatingHours();
        boolean hasActiveDay = false;
        if (oh != null) {
            for (OperatingDayConfig cfg : oh.values()) {
                if (cfg != null && cfg.isActive()) {
                    hasActiveDay = true;
                    break;
                }
            }
        }
        if (!hasActiveDay) {
            missing.add("Space must have at least one active operating day.");
        }

        // Booking-Type Specific Checks
        String bType = space.getBookingType();
        if (com.eklavya.spotfit.constants.BookingType.SEAT.equals(bType)) {
            BookingConfig bConfig = space.getBookingConfig();
            if (bConfig instanceof SeatBookingConfig) {
                SeatBookingConfig sCfg = (SeatBookingConfig) bConfig;
                if (!com.eklavya.spotfit.constants.SeatSelectionMode.AUTO_ASSIGN.equals(sCfg.getSelectionMode())) {
                    List<Seat> activeSeats = new ArrayList<>();
                    if (seats != null) {
                        for (Seat s : seats) {
                            if (s != null && s.isActive()) {
                                activeSeats.add(s);
                            }
                        }
                    }
                    if (activeSeats.isEmpty()) {
                        missing.add("SEAT space requires at least one active Seat unit when selection mode is "
                                + sCfg.getSelectionMode() + ".");
                    } else {
                        List<String> codes = new ArrayList<>();
                        boolean hasAvailableSeat = false;
                        int totalSeatCap = 0;
                        for (Seat s : activeSeats) {
                            if (s.getCapacity() <= 0) {
                                missing.add("Seat '" + s.getSeatCode() + "' capacity must be greater than zero.");
                            }
                            if (!com.eklavya.spotfit.constants.SeatStatus.isValid(s.getStatus())) {
                                missing.add("Seat '" + s.getSeatCode() + "' status is invalid: " + s.getStatus());
                            }
                            if (com.eklavya.spotfit.constants.SeatStatus.AVAILABLE.equals(s.getStatus())) {
                                hasAvailableSeat = true;
                            }
                            String normCode = s.getSeatCode() != null ? s.getSeatCode().trim().toUpperCase() : "";
                            if (codes.contains(normCode)) {
                                missing.add("Duplicate seat code found: " + s.getSeatCode());
                            } else {
                                codes.add(normCode);
                            }
                            totalSeatCap += s.getCapacity();
                        }
                        if (!hasAvailableSeat) {
                            missing.add("At least one Seat must be AVAILABLE.");
                        }
                        if (space.getCapacity() > 0 && totalSeatCap > space.getCapacity()) {
                            missing.add("Total seat capacity (" + totalSeatCap + ") exceeds space capacity ("
                                    + space.getCapacity() + ").");
                        }
                    }
                }
            }
        } else if (com.eklavya.spotfit.constants.BookingType.SLOT.equals(bType)
                || com.eklavya.spotfit.constants.BookingType.PRACTICE_ROOM.equals(bType)
                || com.eklavya.spotfit.constants.BookingType.VENUE.equals(bType)) {
            boolean hasActiveRule = false;
            if (spaceAvailabilityRules != null) {
                for (AvailabilityRule r : spaceAvailabilityRules) {
                    if (r != null && r.isActive()) {
                        hasActiveRule = true;
                        break;
                    }
                }
            }
            if (!hasActiveRule) {
                missing.add(bType + " space requires at least one active direct Space availability rule.");
            }
        } else if (com.eklavya.spotfit.constants.BookingType.WORKSHOP.equals(bType)) {
            long now = System.currentTimeMillis();
            List<WorkshopSession> activeFutureSessions = new ArrayList<>();
            if (sessions != null) {
                for (WorkshopSession ws : sessions) {
                    if (ws != null && ws.isActive()
                            && com.eklavya.spotfit.constants.WorkshopSessionStatus.SCHEDULED.equals(ws.getStatus())
                            && ws.getStartAt() > now) {
                        activeFutureSessions.add(ws);
                    }
                }
            }
            if (activeFutureSessions.isEmpty()) {
                missing.add("Add at least one future scheduled Workshop Session using Manage Sessions.");
            } else {
                BookingConfig bConfig = space.getBookingConfig();
                WorkshopBookingConfig wCfg = (bConfig instanceof WorkshopBookingConfig)
                        ? (WorkshopBookingConfig) bConfig
                        : null;
                for (WorkshopSession ws : activeFutureSessions) {
                    if (ws.getCapacity() <= 0) {
                        missing.add("Workshop Session capacity must be greater than zero.");
                    }
                    if (wCfg != null && wCfg.getMaximumParticipantsPerBooking() > 0
                            && ws.getCapacity() > wCfg.getMaximumParticipantsPerBooking()) {
                        missing.add(
                                "Session capacity (" + ws.getCapacity() + ") exceeds maximum participants per booking ("
                                        + wCfg.getMaximumParticipantsPerBooking() + ").");
                    }
                }
                // Check overlaps
                for (int i = 0; i < activeFutureSessions.size(); i++) {
                    WorkshopSession s1 = activeFutureSessions.get(i);
                    for (int j = i + 1; j < activeFutureSessions.size(); j++) {
                        WorkshopSession s2 = activeFutureSessions.get(j);
                        if (s1.getStartAt() < s2.getEndAt() && s1.getEndAt() > s2.getStartAt()) {
                            missing.add("Overlapping active workshop sessions detected.");
                            break;
                        }
                    }
                }
            }
        } else if (com.eklavya.spotfit.constants.BookingType.RENTAL.equals(bType)) {
            BookingConfig bConfig = space.getBookingConfig();
            if (bConfig instanceof RentalBookingConfig) {
                RentalBookingConfig rCfg = (RentalBookingConfig) bConfig;
                if (rCfg.getAvailableQuantity() <= 0) {
                    missing.add("RENTAL space available quantity must be greater than zero.");
                }
                if (!rCfg.isPickupAvailable() && !rCfg.isDeliveryAvailable()) {
                    missing.add("RENTAL space must have either pickup or delivery enabled.");
                }
            }
        }

        boolean ready = missing.isEmpty();
        return OperationResult.success(new SubmissionReadinessResult(ready, missing));
    }
}
