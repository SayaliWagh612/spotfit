package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.AvailabilityBlockStatus;
import com.eklavya.spotfit.dao.AvailabilityDao;
import com.eklavya.spotfit.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityServiceTest {

    private AvailabilityDao stubDao;
    private AvailabilityService service;
    private Space testSpace;
    private Resource testResource;

    @BeforeEach
    void setUp() {
        stubDao = new AvailabilityDao();
        service = new AvailabilityService(stubDao);

        testSpace = new Space(
                "sp-123", "biz-123", "owner-123", "Dawn Studio", "Desc",
                "CREATIVITY", "PODCASTING", "STUDIO", "123 Main", "Near Park",
                "Mumbai", "Maharashtra", "400001", 10,
                List.of("WiFi"), List.of("Podcast"), List.of("No noise"),
                "09:00", "18:00", List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"), "Asia/Kolkata",
                "DRAFT", true, System.currentTimeMillis(), System.currentTimeMillis()
        );

        testResource = new Resource(
                "res-123", "sp-123", "biz-123", "owner-123", "Podcast Room A", "Desc", "ROOM", "RES-A",
                5, List.of("Mic"), List.of("WiFi"), "AVAILABLE", true, System.currentTimeMillis(), System.currentTimeMillis()
        );
    }

    @Test
    @DisplayName("validateWeeklyRuleInputs: Valid inputs pass")
    void testValidateWeeklyRule_Success() {
        OperationResult<Void> res = service.validateWeeklyRuleInputs(
                testSpace, "MONDAY", "10:00", "16:00", 60, 60, 240);
        assertTrue(res.isSuccess());
    }

    @Test
    @DisplayName("validateWeeklyRuleInputs: Invalid day fails")
    void testValidateWeeklyRule_InvalidDay() {
        OperationResult<Void> res = service.validateWeeklyRuleInputs(
                testSpace, "INVALID_DAY", "10:00", "16:00", 60, 60, 240);
        assertTrue(res.isFailure());
        assertEquals("WEEKDAY_INVALID", res.getErrorCode());
    }

    @Test
    @DisplayName("validateWeeklyRuleInputs: Invalid time format fails")
    void testValidateWeeklyRule_InvalidTimeFormat() {
        OperationResult<Void> res = service.validateWeeklyRuleInputs(
                testSpace, "MONDAY", "10:00 AM", "16:00", 60, 60, 240);
        assertTrue(res.isFailure());
        assertEquals("TIME_FORMAT_INVALID", res.getErrorCode());
    }

    @Test
    @DisplayName("validateWeeklyRuleInputs: Start time after end time fails")
    void testValidateWeeklyRule_StartAfterEnd() {
        OperationResult<Void> res = service.validateWeeklyRuleInputs(
                testSpace, "MONDAY", "17:00", "16:00", 60, 60, 240);
        assertTrue(res.isFailure());
        assertEquals("TIME_ORDER_INVALID", res.getErrorCode());
    }

    @Test
    @DisplayName("validateWeeklyRuleInputs: Duration not aligned with interval fails")
    void testValidateWeeklyRule_DurationUnaligned() {
        OperationResult<Void> res = service.validateWeeklyRuleInputs(
                testSpace, "MONDAY", "10:00", "16:00", 60, 45, 240);
        assertTrue(res.isFailure());
        assertEquals("DURATION_ALIGNMENT_INVALID", res.getErrorCode());
    }

    @Test
    @DisplayName("validateWeeklyRuleInputs: Outside space operating days fails")
    void testValidateWeeklyRule_OutsideSpaceDays() {
        OperationResult<Void> res = service.validateWeeklyRuleInputs(
                testSpace, "SUNDAY", "10:00", "16:00", 60, 60, 240);
        assertTrue(res.isFailure());
        assertEquals("RULE_OUTSIDE_SPACE_DAYS", res.getErrorCode());
    }

    @Test
    @DisplayName("validateWeeklyRuleInputs: Outside space operating hours fails")
    void testValidateWeeklyRule_OutsideSpaceHours() {
        OperationResult<Void> res = service.validateWeeklyRuleInputs(
                testSpace, "MONDAY", "08:00", "16:00", 60, 60, 240);
        assertTrue(res.isFailure());
        assertEquals("RULE_OUTSIDE_SPACE_HOURS", res.getErrorCode());
    }

    @Test
    @DisplayName("validateBlockInputs: Valid block passes")
    void testValidateBlock_Success() {
        long now = System.currentTimeMillis();
        OperationResult<Void> res = service.validateBlockInputs(
                now + 1000, now + 5000, "Maintenance", new ArrayList<>());
        assertTrue(res.isSuccess());
    }

    @Test
    @DisplayName("validateBlockInputs: End before start fails")
    void testValidateBlock_EndBeforeStart() {
        long now = System.currentTimeMillis();
        OperationResult<Void> res = service.validateBlockInputs(
                now + 5000, now + 1000, "Maintenance", new ArrayList<>());
        assertTrue(res.isFailure());
        assertEquals("BLOCK_ORDER_INVALID", res.getErrorCode());
    }

    @Test
    @DisplayName("validateBlockInputs: Blank reason fails")
    void testValidateBlock_BlankReason() {
        long now = System.currentTimeMillis();
        OperationResult<Void> res = service.validateBlockInputs(
                now + 1000, now + 5000, "   ", new ArrayList<>());
        assertTrue(res.isFailure());
        assertEquals("BLOCK_REASON_REQUIRED", res.getErrorCode());
    }

    @Test
    @DisplayName("validateBlockInputs: Duplicate overlapping active block fails")
    void testValidateBlock_DuplicateOverlap() {
        long now = System.currentTimeMillis();
        AvailabilityBlock existing = new AvailabilityBlock(
                "blk-1", "sp-123", "res-123", "owner-123",
                now + 2000, now + 6000, "Event", AvailabilityBlockStatus.ACTIVE, now, now);

        OperationResult<Void> res = service.validateBlockInputs(
                now + 3000, now + 7000, "Repairs", List.of(existing));
        assertTrue(res.isFailure());
        assertEquals("BLOCK_OVERLAP_DUPLICATE", res.getErrorCode());
    }

    @Test
    @DisplayName("generateAvailableSlots: Inactive or null rule produces zero slots")
    void testGenerateSlots_InactiveRule() {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        AvailabilityRule inactiveRule = new AvailabilityRule(
                futureDate.getDayOfWeek().name(), "sp-123", "res-123", "owner-123",
                futureDate.getDayOfWeek().name(), "09:00", "18:00", 60, 60, 240, false, 0L, 0L);

        OperationResult<List<AvailableSlot>> res = service.generateAvailableSlots(
                testSpace, testResource, futureDate, inactiveRule, new ArrayList<>());

        assertTrue(res.isSuccess());
        assertTrue(res.getData().isEmpty());
    }

    @Test
    @DisplayName("generateAvailableSlots: Generates slots at 60m interval minus active block")
    void testGenerateSlots_ActiveBlockSubtraction() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        String dayName = futureDate.getDayOfWeek().name();

        AvailabilityRule rule = new AvailabilityRule(
                dayName, "sp-123", "res-123", "owner-123",
                dayName, "09:00", "13:00", 60, 60, 240, true, 0L, 0L);

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        ZonedDateTime blockStart = ZonedDateTime.of(futureDate, java.time.LocalTime.of(10, 30), zoneId);
        ZonedDateTime blockEnd = ZonedDateTime.of(futureDate, java.time.LocalTime.of(11, 30), zoneId);

        AvailabilityBlock block = new AvailabilityBlock(
                "blk-1", "sp-123", "res-123", "owner-123",
                blockStart.toInstant().toEpochMilli(), blockEnd.toInstant().toEpochMilli(),
                "Cleaning", AvailabilityBlockStatus.ACTIVE, 0L, 0L);

        OperationResult<List<AvailableSlot>> res = service.generateAvailableSlots(
                testSpace, testResource, futureDate, rule, List.of(block));

        assertTrue(res.isSuccess());
        List<AvailableSlot> slots = res.getData();
        assertEquals(2, slots.size());
        assertEquals("09:00", slots.get(0).getLocalStartTime());
        assertEquals("10:00", slots.get(0).getLocalEndTime());
        assertEquals("12:00", slots.get(1).getLocalStartTime());
        assertEquals("13:00", slots.get(1).getLocalEndTime());
    }

    @Test
    @DisplayName("generateAvailableSlots: Cancelled block is ignored")
    void testGenerateSlots_CancelledBlockIgnored() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        String dayName = futureDate.getDayOfWeek().name();

        AvailabilityRule rule = new AvailabilityRule(
                dayName, "sp-123", "res-123", "owner-123",
                dayName, "09:00", "12:00", 60, 60, 240, true, 0L, 0L);

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        ZonedDateTime blockStart = ZonedDateTime.of(futureDate, java.time.LocalTime.of(10, 0), zoneId);
        ZonedDateTime blockEnd = ZonedDateTime.of(futureDate, java.time.LocalTime.of(11, 0), zoneId);

        AvailabilityBlock block = new AvailabilityBlock(
                "blk-1", "sp-123", "res-123", "owner-123",
                blockStart.toInstant().toEpochMilli(), blockEnd.toInstant().toEpochMilli(),
                "Cancelled Repair", AvailabilityBlockStatus.CANCELLED, 0L, 0L);

        OperationResult<List<AvailableSlot>> res = service.generateAvailableSlots(
                testSpace, testResource, futureDate, rule, List.of(block));

        assertTrue(res.isSuccess());
        assertEquals(3, res.getData().size());
    }

    @Test
    @DisplayName("generateAvailableSlots: Boundary-touching block does not remove slot")
    void testGenerateSlots_BoundaryTouching() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        String dayName = futureDate.getDayOfWeek().name();

        AvailabilityRule rule = new AvailabilityRule(
                dayName, "sp-123", "res-123", "owner-123",
                dayName, "09:00", "12:00", 60, 60, 240, true, 0L, 0L);

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        ZonedDateTime blockStart = ZonedDateTime.of(futureDate, java.time.LocalTime.of(8, 0), zoneId);
        ZonedDateTime blockEnd = ZonedDateTime.of(futureDate, java.time.LocalTime.of(9, 0), zoneId);

        AvailabilityBlock block = new AvailabilityBlock(
                "blk-1", "sp-123", "res-123", "owner-123",
                blockStart.toInstant().toEpochMilli(), blockEnd.toInstant().toEpochMilli(),
                "Early Setup", AvailabilityBlockStatus.ACTIVE, 0L, 0L);

        OperationResult<List<AvailableSlot>> res = service.generateAvailableSlots(
                testSpace, testResource, futureDate, rule, List.of(block));

        assertTrue(res.isSuccess());
        assertEquals(3, res.getData().size());
        assertEquals("09:00", res.getData().get(0).getLocalStartTime());
    }

    @Test
    @DisplayName("Normalization: Mixed-case and short day values normalize to controlled uppercase constants")
    void testDayNormalization() {
        assertEquals(com.eklavya.spotfit.constants.WeekDay.MONDAY, com.eklavya.spotfit.constants.WeekDay.normalizeDay("Monday"));
        assertEquals(com.eklavya.spotfit.constants.WeekDay.MONDAY, com.eklavya.spotfit.constants.WeekDay.normalizeDay("monday"));
        assertEquals(com.eklavya.spotfit.constants.WeekDay.MONDAY, com.eklavya.spotfit.constants.WeekDay.normalizeDay("MON"));
        assertEquals(com.eklavya.spotfit.constants.WeekDay.FRIDAY, com.eklavya.spotfit.constants.WeekDay.normalizeDay("Fri"));
    }

    @Test
    @DisplayName("Unchecked day without existing rule does not write and produces success")
    void testUncheckedDay_NoExistingRule() {
        OperationResult<AvailabilityRule> res = service.saveWeeklyRule(
                testSpace, testResource, "SATURDAY", "09:00", "18:00", 60, 60, 240, false, "token-123").join();
        assertTrue(res.isSuccess());
        assertNull(res.getData());
    }

    @Test
    @DisplayName("Monday outside Space operating days rejected with RULE_OUTSIDE_SPACE_DAYS")
    void testMondayOutsideOperatingDays_Rejected() {
        Space wedToFriSpace = new Space(
                "sp-wed", "biz-1", "owner-123", "Wed-Fri Studio", "Desc",
                "CREATIVITY", "PODCASTING", "STUDIO", "Address", "Landmark",
                "City", "State", "400001", 10, List.of(), List.of(), List.of(),
                "09:00", "18:00", List.of("WEDNESDAY", "THURSDAY", "FRIDAY"), "Asia/Kolkata",
                "DRAFT", true, 0L, 0L
        );

        OperationResult<Void> valRes = service.validateWeeklyRuleInputs(
                wedToFriSpace, "Monday", "09:00", "18:00", 60, 60, 240);

        assertTrue(valRes.isFailure());
        assertEquals("RULE_OUTSIDE_SPACE_DAYS", valRes.getErrorCode());
    }
}
