package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.AvailabilityBlockStatus;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.WeekDay;
import com.eklavya.spotfit.dao.AvailabilityDao;
import com.eklavya.spotfit.model.AvailabilityBlock;
import com.eklavya.spotfit.model.AvailabilityRule;
import com.eklavya.spotfit.model.AvailableSlot;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.TimeInterval;
import com.eklavya.spotfit.util.ValidationUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service enforcing business rules and pure slot generation logic for Resource Availability.
 */
public class AvailabilityService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final AvailabilityDao availabilityDao;

    public AvailabilityService() {
        this.availabilityDao = new AvailabilityDao();
    }

    public AvailabilityService(AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
    }

    // --- Weekly Rules ---

    public CompletableFuture<OperationResult<AvailabilityRule>> saveWeeklyRule(
            Space space,
            Resource resource,
            String dayOfWeek,
            String startTime,
            String endTime,
            int slotIntervalMinutes,
            int minimumBookingMinutes,
            int maximumBookingMinutes,
            boolean active,
            String idToken) {

        if (space == null || space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space is required."));
        }
        if (resource == null || resource.getResourceId() == null || resource.getResourceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource is required."));
        }

        String normDay = WeekDay.normalizeDay(dayOfWeek);
        if (normDay == null || !WeekDay.isValid(normDay)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("WEEKDAY_INVALID", "Invalid day of week."));
        }

        if (!active) {
            return availabilityDao.getAvailabilityRule(space.getSpaceId(), resource.getResourceId(), normDay, idToken)
                    .thenCompose(getRes -> {
                        if (getRes.isSuccess() && getRes.getData() != null) {
                            AvailabilityRule existing = getRes.getData();
                            existing.setActive(false);
                            existing.setUpdatedAt(System.currentTimeMillis());
                            return availabilityDao.setAvailabilityRule(space.getSpaceId(), resource.getResourceId(), existing, idToken);
                        } else {
                            return CompletableFuture.completedFuture(
                                    OperationResult.success(null, "No rule to deactivate."));
                        }
                    });
        }

        OperationResult<Void> valResult = validateWeeklyRuleInputs(
                space, normDay, startTime, endTime, slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes);

        if (valResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(valResult.getErrorCode(), valResult.getMessage()));
        }

        long now = System.currentTimeMillis();

        AvailabilityRule rule = new AvailabilityRule(
                normDay, space.getSpaceId(), resource.getResourceId(), space.getOwnerId(),
                normDay, startTime.trim(), endTime.trim(),
                slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes,
                true, now, now);

        return availabilityDao.setAvailabilityRule(space.getSpaceId(), resource.getResourceId(), rule, idToken);
    }

    public CompletableFuture<OperationResult<List<AvailabilityRule>>> loadWeeklyRules(
            String spaceId,
            String resourceId,
            String idToken) {

        return availabilityDao.getAvailabilityRules(spaceId, resourceId, idToken);
    }

    public OperationResult<Void> validateWeeklyRuleInputs(
            Space space,
            String dayOfWeek,
            String startTime,
            String endTime,
            int slotIntervalMinutes,
            int minimumBookingMinutes,
            int maximumBookingMinutes) {

        String normDay = WeekDay.normalizeDay(dayOfWeek);
        if (!WeekDay.isValid(normDay)) {
            return OperationResult.failure("WEEKDAY_INVALID", "Invalid day of week.");
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startTime.trim(), TIME_FORMATTER);
            end = LocalTime.parse(endTime.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException | NullPointerException e) {
            return OperationResult.failure("TIME_FORMAT_INVALID", "Start time and End time must be in strict HH:mm 24-hour format.");
        }

        if (!start.isBefore(end)) {
            return OperationResult.failure("TIME_ORDER_INVALID", "Start time must be earlier than end time.");
        }

        if (slotIntervalMinutes <= 0) {
            return OperationResult.failure("SLOT_INTERVAL_INVALID", "Slot interval must be positive.");
        }

        if (minimumBookingMinutes <= 0) {
            return OperationResult.failure("MIN_DURATION_INVALID", "Minimum booking duration must be positive.");
        }

        if (maximumBookingMinutes < minimumBookingMinutes) {
            return OperationResult.failure("MAX_DURATION_INVALID", "Maximum booking duration must be greater than or equal to minimum duration.");
        }

        if (minimumBookingMinutes % slotIntervalMinutes != 0 || maximumBookingMinutes % slotIntervalMinutes != 0) {
            return OperationResult.failure("DURATION_ALIGNMENT_INVALID", "Minimum and maximum durations must align with the slot interval.");
        }

        if (space != null) {
            List<String> opDays = space.getOperatingDays();
            if (opDays != null && !opDays.isEmpty()) {
                boolean dayMatches = false;
                for (String d : opDays) {
                    String normOpDay = WeekDay.normalizeDay(d);
                    if (normDay.equals(normOpDay)) {
                        dayMatches = true;
                        break;
                    }
                }
                if (!dayMatches) {
                    return OperationResult.failure("RULE_OUTSIDE_SPACE_DAYS", "Resource rule day is outside Space operating days.");
                }
            }

            if (space.getOpeningTime() != null && space.getClosingTime() != null) {
                try {
                    LocalTime spaceOpen = LocalTime.parse(space.getOpeningTime().trim(), TIME_FORMATTER);
                    LocalTime spaceClose = LocalTime.parse(space.getClosingTime().trim(), TIME_FORMATTER);
                    if (start.isBefore(spaceOpen) || end.isAfter(spaceClose)) {
                        return OperationResult.failure("RULE_OUTSIDE_SPACE_HOURS", "Resource rule hours must stay within Space operating hours.");
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return OperationResult.success(null);
    }

    // --- Availability Blocks ---

    public CompletableFuture<OperationResult<AvailabilityBlock>> createBlock(
            Space space,
            Resource resource,
            long startAt,
            long endAt,
            String reason,
            List<AvailabilityBlock> existingBlocks,
            String idToken) {

        if (space == null || space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space is required."));
        }
        if (resource == null || resource.getResourceId() == null || resource.getResourceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource is required."));
        }

        OperationResult<Void> valResult = validateBlockInputs(startAt, endAt, reason, existingBlocks);
        if (valResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(valResult.getErrorCode(), valResult.getMessage()));
        }

        String blockId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        AvailabilityBlock block = new AvailabilityBlock(
                blockId, space.getSpaceId(), resource.getResourceId(), space.getOwnerId(),
                startAt, endAt, ValidationUtil.sanitizeText(reason), AvailabilityBlockStatus.ACTIVE,
                now, now);

        return availabilityDao.createAvailabilityBlock(space.getSpaceId(), resource.getResourceId(), block, idToken);
    }

    public CompletableFuture<OperationResult<List<AvailabilityBlock>>> loadBlocks(
            String spaceId,
            String resourceId,
            String idToken) {

        return availabilityDao.getAvailabilityBlocks(spaceId, resourceId, idToken);
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> cancelBlock(
            String spaceId,
            String resourceId,
            String blockId,
            String idToken) {

        return availabilityDao.getAvailabilityBlock(spaceId, resourceId, blockId, idToken)
                .thenCompose(res -> {
                    if (res.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                    AvailabilityBlock block = res.getData();
                    if (!AvailabilityBlockStatus.ACTIVE.equalsIgnoreCase(block.getStatus())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure("BLOCK_ALREADY_CANCELLED", "Availability block is already cancelled."));
                    }

                    block.setStatus(AvailabilityBlockStatus.CANCELLED);
                    block.setUpdatedAt(System.currentTimeMillis());
                    return availabilityDao.updateAvailabilityBlock(spaceId, resourceId, block, idToken);
                });
    }

    public OperationResult<Void> validateBlockInputs(
            long startAt,
            long endAt,
            String reason,
            List<AvailabilityBlock> existingBlocks) {

        if (startAt <= 0 || endAt <= 0) {
            return OperationResult.failure("BLOCK_TIMESTAMP_INVALID", "Start and End timestamps must be positive epoch milliseconds.");
        }

        if (endAt <= startAt) {
            return OperationResult.failure("BLOCK_ORDER_INVALID", "End time must be after start time.");
        }

        if (ValidationUtil.isBlank(reason)) {
            return OperationResult.failure("BLOCK_REASON_REQUIRED", "Reason is required for availability block.");
        }

        if (existingBlocks != null) {
            for (AvailabilityBlock existing : existingBlocks) {
                if (existing != null && existing.isActive()) {
                    if (overlaps(startAt, endAt, existing.getStartAt(), existing.getEndAt())) {
                        return OperationResult.failure("BLOCK_OVERLAP_DUPLICATE",
                                "This Resource already has an availability block during that period.");
                    }
                }
            }
        }

        return OperationResult.success(null);
    }

    // --- Pure Slot Generation Algorithm ---

    /**
     * Pure, testable slot generation function.
     * Computes candidate slots for a specific LocalDate using the Space timezone and weekly rule,
     * subtracting active availability blocks and past times.
     */
    public OperationResult<List<AvailableSlot>> generateAvailableSlots(
            Space space,
            Resource resource,
            LocalDate date,
            AvailabilityRule rule,
            List<AvailabilityBlock> blocks) {

        if (date == null) {
            return OperationResult.failure("DATE_REQUIRED", "Date is required for slot generation.");
        }

        if (rule == null || !rule.isActive()) {
            return OperationResult.success(new ArrayList<>());
        }

        String targetDay = date.getDayOfWeek().name();
        if (!targetDay.equalsIgnoreCase(rule.getDayOfWeek())) {
            return OperationResult.success(new ArrayList<>());
        }

        ZoneId zoneId;
        try {
            String tz = (space != null && space.getTimeZoneId() != null) ? space.getTimeZoneId() : Space.DEFAULT_TIMEZONE;
            zoneId = ZoneId.of(tz);
        } catch (Exception e) {
            zoneId = ZoneId.of(Space.DEFAULT_TIMEZONE);
        }

        LocalTime startTime;
        LocalTime endTime;
        try {
            startTime = LocalTime.parse(rule.getStartTime().trim(), TIME_FORMATTER);
            endTime = LocalTime.parse(rule.getEndTime().trim(), TIME_FORMATTER);
        } catch (Exception e) {
            return OperationResult.failure("RULE_TIME_INVALID", "Rule start/end times could not be parsed.");
        }

        int intervalMinutes = rule.getSlotIntervalMinutes();
        if (intervalMinutes <= 0) {
            return OperationResult.failure("INTERVAL_INVALID", "Slot interval must be greater than zero.");
        }

        ZonedDateTime ruleStartZdt = ZonedDateTime.of(date, startTime, zoneId);
        ZonedDateTime ruleEndZdt = ZonedDateTime.of(date, endTime, zoneId);

        long ruleStartEpoch = ruleStartZdt.toInstant().toEpochMilli();
        long ruleEndEpoch = ruleEndZdt.toInstant().toEpochMilli();

        // Build list of unavailable periods (convert ACTIVE blocks to TimeInterval)
        List<TimeInterval> unavailablePeriods = new ArrayList<>();
        if (blocks != null) {
            for (AvailabilityBlock block : blocks) {
                if (block != null && block.isActive()) {
                    unavailablePeriods.add(new TimeInterval(block.getStartAt(), block.getEndAt()));
                }
            }
        }

        long nowEpoch = System.currentTimeMillis();
        List<AvailableSlot> slots = new ArrayList<>();

        long currentStartEpoch = ruleStartEpoch;

        while (currentStartEpoch + ((long) intervalMinutes * 60 * 1000) <= ruleEndEpoch) {
            long currentEndEpoch = currentStartEpoch + ((long) intervalMinutes * 60 * 1000);

            // Skip past slots
            if (currentStartEpoch >= nowEpoch) {
                boolean hasConflict = overlapsUnavailablePeriod(currentStartEpoch, currentEndEpoch, unavailablePeriods);

                if (!hasConflict) {
                    ZonedDateTime slotStartZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentStartEpoch), zoneId);
                    ZonedDateTime slotEndZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentEndEpoch), zoneId);

                    String localStart = slotStartZdt.format(TIME_FORMATTER);
                    String localEnd = slotEndZdt.format(TIME_FORMATTER);

                    AvailableSlot slot = new AvailableSlot(
                            space != null ? space.getSpaceId() : null,
                            resource != null ? resource.getResourceId() : null,
                            currentStartEpoch,
                            currentEndEpoch,
                            localStart,
                            localEnd,
                            true);

                    slots.add(slot);
                }
            }

            currentStartEpoch = currentEndEpoch;
        }

        slots.sort(Comparator.comparingLong(AvailableSlot::getStartAt));
        return OperationResult.success(slots);
    }

    /**
     * Conflict checking extension point.
     * Evaluates if [candidateStart, candidateEnd] overlaps any interval in unavailablePeriods.
     */
    public boolean overlapsUnavailablePeriod(long candidateStart, long candidateEnd, List<TimeInterval> unavailablePeriods) {
        if (unavailablePeriods == null || unavailablePeriods.isEmpty()) {
            return false;
        }
        for (TimeInterval interval : unavailablePeriods) {
            if (interval.overlaps(candidateStart, candidateEnd)) {
                return true;
            }
        }
        return false;
    }

    private boolean overlaps(long start1, long end1, long start2, long end2) {
        return start1 < end2 && end1 > start2;
    }

    // --- Additive V2 Direct Space Availability Methods ---

    public CompletableFuture<OperationResult<AvailabilityRule>> saveSpaceWeeklyRule(
            Space space,
            String dayOfWeek,
            String startTime,
            String endTime,
            int slotIntervalMinutes,
            int minimumBookingMinutes,
            int maximumBookingMinutes,
            boolean active,
            String idToken) {

        if (space == null || space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space is required."));
        }
        if (space.getSchemaVersion() != 2) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SPACE_NOT_V2", "Space must be schema version 2 for direct space availability."));
        }

        String normDay = WeekDay.normalizeDay(dayOfWeek);
        if (normDay == null || !WeekDay.isValid(normDay)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("WEEKDAY_INVALID", "Invalid day of week."));
        }

        if (!active) {
            return availabilityDao.getSpaceAvailabilityRule(space.getSpaceId(), normDay, idToken)
                    .thenCompose(getRes -> {
                        if (getRes.isSuccess() && getRes.getData() != null) {
                            AvailabilityRule existing = getRes.getData();
                            existing.setActive(false);
                            existing.setUpdatedAt(System.currentTimeMillis());
                            return availabilityDao.setSpaceAvailabilityRule(space.getSpaceId(), existing, idToken);
                        } else {
                            return CompletableFuture.completedFuture(
                                    OperationResult.success(null, "No rule to deactivate."));
                        }
                    });
        }

        OperationResult<Void> valResult = validateSpaceWeeklyRuleInputs(
                space, normDay, startTime, endTime, slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes);

        if (valResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(valResult.getErrorCode(), valResult.getMessage()));
        }

        long now = System.currentTimeMillis();

        AvailabilityRule rule = new AvailabilityRule(
                normDay, space.getSpaceId(), "", space.getOwnerId(),
                normDay, startTime.trim(), endTime.trim(),
                slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes,
                true, now, now);

        return availabilityDao.setSpaceAvailabilityRule(space.getSpaceId(), rule, idToken);
    }

    public CompletableFuture<OperationResult<List<AvailabilityRule>>> loadSpaceWeeklyRules(
            String spaceId,
            String idToken) {

        return availabilityDao.getSpaceAvailabilityRules(spaceId, idToken);
    }

    public OperationResult<Void> validateSpaceWeeklyRuleInputs(
            Space space,
            String dayOfWeek,
            String startTime,
            String endTime,
            int slotIntervalMinutes,
            int minimumBookingMinutes,
            int maximumBookingMinutes) {

        if (space == null || space.getSchemaVersion() != 2) {
            return OperationResult.failure("SPACE_NOT_V2", "Space must be schema version 2 for direct space availability.");
        }

        String normDay = WeekDay.normalizeDay(dayOfWeek);
        if (!WeekDay.isValid(normDay)) {
            return OperationResult.failure("WEEKDAY_INVALID", "Invalid day of week.");
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startTime.trim(), TIME_FORMATTER);
            end = LocalTime.parse(endTime.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException | NullPointerException e) {
            return OperationResult.failure("TIME_FORMAT_INVALID", "Start time and End time must be in strict HH:mm 24-hour format.");
        }

        if (!start.isBefore(end)) {
            return OperationResult.failure("TIME_ORDER_INVALID", "Start time must be earlier than end time.");
        }

        if (slotIntervalMinutes <= 0) {
            return OperationResult.failure("SLOT_INTERVAL_INVALID", "Slot interval must be positive.");
        }

        if (minimumBookingMinutes <= 0) {
            return OperationResult.failure("MIN_DURATION_INVALID", "Minimum booking duration must be positive.");
        }

        if (maximumBookingMinutes < minimumBookingMinutes) {
            return OperationResult.failure("MAX_DURATION_INVALID", "Maximum booking duration must be greater than or equal to minimum duration.");
        }

        if (minimumBookingMinutes % slotIntervalMinutes != 0 || maximumBookingMinutes % slotIntervalMinutes != 0) {
            return OperationResult.failure("DURATION_ALIGNMENT_INVALID", "Minimum and maximum durations must align with the slot interval.");
        }

        // Validate day of week against Space operating hours map if present, or operating days
        com.eklavya.spotfit.model.OperatingDayConfig dayConfig = space.getOperatingHours() != null ? space.getOperatingHours().get(normDay) : null;
        if (dayConfig != null) {
            if (!dayConfig.isActive()) {
                return OperationResult.failure("RULE_OUTSIDE_SPACE_DAYS", "Space rule day is outside Space operating days.");
            }
        } else {
            List<String> opDays = space.getOperatingDays();
            if (opDays != null && !opDays.isEmpty()) {
                boolean dayMatches = false;
                for (String d : opDays) {
                    if (normDay.equals(WeekDay.normalizeDay(d))) {
                        dayMatches = true;
                        break;
                    }
                }
                if (!dayMatches) {
                    return OperationResult.failure("RULE_OUTSIDE_SPACE_DAYS", "Space rule day is outside Space operating days.");
                }
            }
        }

        return OperationResult.success(null);
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> createSpaceBlock(
            Space space,
            long startAt,
            long endAt,
            String reason,
            String idToken) {

        if (space == null || space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space is required."));
        }
        if (space.getSchemaVersion() != 2) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SPACE_NOT_V2", "Space must be schema version 2 for direct space availability."));
        }

        // Authoritatively load existing blocks from DAO
        return availabilityDao.getSpaceAvailabilityBlocks(space.getSpaceId(), idToken)
                .thenCompose(blocksRes -> {
                    List<AvailabilityBlock> existingBlocks = blocksRes.isSuccess() ? blocksRes.getData() : new ArrayList<>();

                    OperationResult<Void> valResult = validateBlockInputs(startAt, endAt, reason, existingBlocks);
                    if (valResult.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(valResult.getErrorCode(), valResult.getMessage()));
                    }

                    String blockId = UUID.randomUUID().toString();
                    long now = System.currentTimeMillis();

                    AvailabilityBlock block = new AvailabilityBlock(
                            blockId, space.getSpaceId(), "", space.getOwnerId(),
                            startAt, endAt, ValidationUtil.sanitizeText(reason), AvailabilityBlockStatus.ACTIVE,
                            now, now);

                    return availabilityDao.createSpaceAvailabilityBlock(space.getSpaceId(), block, idToken);
                });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> createSpaceBlock(
            Space space,
            long startAt,
            long endAt,
            String reason,
            List<AvailabilityBlock> existingBlocks,
            String idToken) {

        if (space == null || space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space is required."));
        }
        if (space.getSchemaVersion() != 2) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("SPACE_NOT_V2", "Space must be schema version 2 for direct space availability."));
        }

        OperationResult<Void> valResult = validateBlockInputs(startAt, endAt, reason, existingBlocks);
        if (valResult.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(valResult.getErrorCode(), valResult.getMessage()));
        }

        String blockId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        AvailabilityBlock block = new AvailabilityBlock(
                blockId, space.getSpaceId(), "", space.getOwnerId(),
                startAt, endAt, ValidationUtil.sanitizeText(reason), AvailabilityBlockStatus.ACTIVE,
                now, now);

        return availabilityDao.createSpaceAvailabilityBlock(space.getSpaceId(), block, idToken);
    }

    public CompletableFuture<OperationResult<List<AvailabilityBlock>>> loadSpaceBlocks(
            String spaceId,
            String idToken) {

        return availabilityDao.getSpaceAvailabilityBlocks(spaceId, idToken);
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> cancelSpaceBlock(
            String spaceId,
            String blockId,
            String idToken) {

        return availabilityDao.getSpaceAvailabilityBlock(spaceId, blockId, idToken)
                .thenCompose(res -> {
                    if (res.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                    AvailabilityBlock block = res.getData();
                    if (!AvailabilityBlockStatus.ACTIVE.equalsIgnoreCase(block.getStatus())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure("BLOCK_ALREADY_CANCELLED", "Availability block is already cancelled."));
                    }

                    block.setStatus(AvailabilityBlockStatus.CANCELLED);
                    block.setUpdatedAt(System.currentTimeMillis());
                    return availabilityDao.updateSpaceAvailabilityBlock(spaceId, block, idToken);
                });
    }

    public OperationResult<List<AvailableSlot>> generateSpaceAvailableSlots(
            Space space,
            LocalDate date,
            AvailabilityRule rule,
            List<AvailabilityBlock> blocks) {

        return generateAvailableSlots(space, null, date, rule, blocks);
    }
}
