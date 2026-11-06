package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.ResourceDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.AvailabilityBlock;
import com.eklavya.spotfit.model.AvailabilityRule;
import com.eklavya.spotfit.model.AvailableSlot;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.AvailabilityService;
import com.eklavya.spotfit.session.AppSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller handling Owner authentication, hierarchy validation, and delegating
 * Availability management operations to AvailabilityService.
 */
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final SpaceDao spaceDao;
    private final ResourceDao resourceDao;

    public AvailabilityController() {
        this.availabilityService = new AvailabilityService();
        this.spaceDao = new SpaceDao();
        this.resourceDao = new ResourceDao();
    }

    public AvailabilityController(AvailabilityService availabilityService, SpaceDao spaceDao, ResourceDao resourceDao) {
        this.availabilityService = availabilityService;
        this.spaceDao = spaceDao;
        this.resourceDao = resourceDao;
    }

    public CompletableFuture<OperationResult<Map<String, Object>>> loadAvailabilityForResource(String spaceId, String resourceId) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<AuthContext> authRes = verifyOwnerAndContext(spaceId, resourceId, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            AuthContext ctx = authRes.getData();

            CompletableFuture<OperationResult<List<AvailabilityRule>>> rulesFut =
                    availabilityService.loadWeeklyRules(spaceId, resourceId, idToken);

            CompletableFuture<OperationResult<List<AvailabilityBlock>>> blocksFut =
                    availabilityService.loadBlocks(spaceId, resourceId, idToken);

            return rulesFut.thenCombine(blocksFut, (rulesRes, blocksRes) -> {
                if (rulesRes.isFailure()) {
                    return OperationResult.failure(rulesRes.getErrorCode(), rulesRes.getMessage());
                }
                if (blocksRes.isFailure()) {
                    return OperationResult.failure(blocksRes.getErrorCode(), blocksRes.getMessage());
                }

                Map<String, Object> data = new HashMap<>();
                data.put("space", ctx.space);
                data.put("resource", ctx.resource);
                data.put("rules", rulesRes.getData());
                data.put("blocks", blocksRes.getData());

                return OperationResult.success(data);
            });
        });
    }

    public CompletableFuture<OperationResult<AvailabilityRule>> saveWeeklyRule(
            String spaceId,
            String resourceId,
            String dayOfWeek,
            String startTime,
            String endTime,
            int slotIntervalMinutes,
            int minimumBookingMinutes,
            int maximumBookingMinutes,
            boolean active) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<AuthContext> authRes = verifyOwnerAndContext(spaceId, resourceId, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            AuthContext ctx = authRes.getData();
            return availabilityService.saveWeeklyRule(
                    ctx.space, ctx.resource, dayOfWeek, startTime, endTime,
                    slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes, active, idToken);
        });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> createAvailabilityBlock(
            String spaceId,
            String resourceId,
            long startAt,
            long endAt,
            String reason) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<AuthContext> authRes = verifyOwnerAndContext(spaceId, resourceId, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            AuthContext ctx = authRes.getData();

            return availabilityService.loadBlocks(spaceId, resourceId, idToken)
                    .thenCompose(blocksRes -> {
                        List<AvailabilityBlock> existing = blocksRes.isSuccess() ? blocksRes.getData() : new ArrayList<>();
                        return availabilityService.createBlock(
                                ctx.space, ctx.resource, startAt, endAt, reason, existing, idToken);
                    });
        });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> cancelAvailabilityBlock(
            String spaceId,
            String resourceId,
            String blockId) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<AuthContext> authRes = verifyOwnerAndContext(spaceId, resourceId, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            return availabilityService.cancelBlock(spaceId, resourceId, blockId, idToken);
        });
    }

    public CompletableFuture<OperationResult<List<AvailableSlot>>> previewSlots(
            String spaceId,
            String resourceId,
            LocalDate date) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<AuthContext> authRes = verifyOwnerAndContext(spaceId, resourceId, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            AuthContext ctx = authRes.getData();
            String dayOfWeek = date != null ? date.getDayOfWeek().name() : null;

            CompletableFuture<OperationResult<List<AvailabilityRule>>> rulesFut =
                    availabilityService.loadWeeklyRules(spaceId, resourceId, idToken);

            CompletableFuture<OperationResult<List<AvailabilityBlock>>> blocksFut =
                    availabilityService.loadBlocks(spaceId, resourceId, idToken);

            return rulesFut.thenCombine(blocksFut, (rulesRes, blocksRes) -> {
                if (rulesRes.isFailure()) {
                    return OperationResult.failure(rulesRes.getErrorCode(), rulesRes.getMessage());
                }
                if (blocksRes.isFailure()) {
                    return OperationResult.failure(blocksRes.getErrorCode(), blocksRes.getMessage());
                }

                List<AvailabilityRule> rules = rulesRes.getData();
                AvailabilityRule targetRule = null;
                if (rules != null && dayOfWeek != null) {
                    for (AvailabilityRule r : rules) {
                        if (r != null && dayOfWeek.equalsIgnoreCase(r.getDayOfWeek())) {
                            targetRule = r;
                            break;
                        }
                    }
                }

                return availabilityService.generateAvailableSlots(
                        ctx.space, ctx.resource, date, targetRule, blocksRes.getData());
            });
        });
    }

    public CompletableFuture<OperationResult<List<AvailabilityRule>>> applySpaceOperatingHoursToResource(
            String spaceId,
            String resourceId) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<AuthContext> authRes = verifyOwnerAndContext(spaceId, resourceId, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            AuthContext ctx = authRes.getData();
            Space space = ctx.space;
            Resource resource = ctx.resource;

            List<String> rawOpDays = space.getOperatingDays();
            String openTime = space.getOpeningTime() != null ? space.getOpeningTime() : "09:00";
            String closeTime = space.getClosingTime() != null ? space.getClosingTime() : "18:00";

            if (rawOpDays == null || rawOpDays.isEmpty()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("SPACE_OPERATING_DAYS_EMPTY", "Space has no operating days configured."));
            }

            List<String> normalizedOpDays = new ArrayList<>();
            for (String d : rawOpDays) {
                String norm = com.eklavya.spotfit.constants.WeekDay.normalizeDay(d);
                if (norm != null && com.eklavya.spotfit.constants.WeekDay.isValid(norm) && !normalizedOpDays.contains(norm)) {
                    normalizedOpDays.add(norm);
                }
            }

            if (normalizedOpDays.isEmpty()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("SPACE_OPERATING_DAYS_EMPTY", "Space has no valid operating days configured."));
            }

            List<CompletableFuture<OperationResult<AvailabilityRule>>> futures = new ArrayList<>();
            List<String> targetDays = new ArrayList<>();

            for (String day : normalizedOpDays) {
                targetDays.add(day);
                futures.add(availabilityService.saveWeeklyRule(
                        space, resource, day, openTime, closeTime, 60, 60, 240, true, idToken));
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            return allOf.thenCompose(v -> {
                List<String> failedDays = new ArrayList<>();
                for (int i = 0; i < futures.size(); i++) {
                    try {
                        OperationResult<AvailabilityRule> res = futures.get(i).join();
                        if (res == null || res.isFailure()) {
                            String dayName = targetDays.get(i);
                            String errorMsg = (res != null) ? res.getMessage() : "Unknown error";
                            failedDays.add(dayName + " (" + errorMsg + ")");
                        }
                    } catch (Exception e) {
                        failedDays.add(targetDays.get(i) + " (" + e.getMessage() + ")");
                    }
                }

                if (!failedDays.isEmpty()) {
                    return CompletableFuture.completedFuture(
                            OperationResult.<List<AvailabilityRule>>failure("BULK_SAVE_PARTIAL_FAILURE",
                                    "Failed to save operating hours for: " + String.join(", ", failedDays)));
                }

                return loadAvailabilityForResource(spaceId, resourceId)
                        .thenApply(res -> {
                            if (res.isFailure()) {
                                return OperationResult.failure(res.getErrorCode(), res.getMessage());
                            }
                            @SuppressWarnings("unchecked")
                            List<AvailabilityRule> rules = (List<AvailabilityRule>) res.getData().get("rules");
                            return OperationResult.success(rules, "Space operating hours applied to resource schedule.");
                        });
            });
        });
    }

    // --- Private Helper ---

    private OperationResult<AuthContext> verifyOwnerAndContext(String spaceId, String resourceId, String idToken) {
        if (!AppSession.isAuthenticated()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User is not authenticated.");
        }
        if (AppSession.getRole() != UserRole.OWNER) {
            return OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Space Owners can manage resource availability.");
        }

        String ownerId = AppSession.getUserId();

        if (spaceId == null || spaceId.isBlank() || resourceId == null || resourceId.isBlank()) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Resource ID are required.");
        }

        try {
            OperationResult<Space> spaceRes = spaceDao.getSpace(spaceId, idToken).get();
            if (spaceRes.isFailure()) {
                return OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage());
            }
            Space space = spaceRes.getData();
            if (!ownerId.equals(space.getOwnerId())) {
                return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied. Space does not belong to authenticated Owner.");
            }

            OperationResult<Resource> resourceRes = resourceDao.getResource(spaceId, resourceId, idToken).get();
            if (resourceRes.isFailure()) {
                return OperationResult.failure(resourceRes.getErrorCode(), resourceRes.getMessage());
            }
            Resource resource = resourceRes.getData();
            if (!spaceId.equals(resource.getSpaceId()) || !ownerId.equals(resource.getOwnerId())) {
                return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied. Resource does not belong to Space/Owner.");
            }

            return OperationResult.success(new AuthContext(space, resource, idToken));

        } catch (Exception e) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Failed to verify Space and Resource ownership context.");
        }
    }

    // --- Additive V2 Direct Space Availability Controller Methods ---

    public CompletableFuture<OperationResult<Map<String, Object>>> loadSpaceAvailability(String spaceId) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, false, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();

            CompletableFuture<OperationResult<List<AvailabilityRule>>> rulesFut =
                    availabilityService.loadSpaceWeeklyRules(spaceId, idToken);

            CompletableFuture<OperationResult<List<AvailabilityBlock>>> blocksFut =
                    availabilityService.loadSpaceBlocks(spaceId, idToken);

            return rulesFut.thenCombine(blocksFut, (rulesRes, blocksRes) -> {
                if (rulesRes.isFailure()) {
                    return OperationResult.failure(rulesRes.getErrorCode(), rulesRes.getMessage());
                }
                if (blocksRes.isFailure()) {
                    return OperationResult.failure(blocksRes.getErrorCode(), blocksRes.getMessage());
                }

                Map<String, Object> data = new HashMap<>();
                data.put("space", ctx.space);
                data.put("rules", rulesRes.getData());
                data.put("blocks", blocksRes.getData());

                return OperationResult.success(data);
            });
        });
    }

    public CompletableFuture<OperationResult<AvailabilityRule>> saveSpaceWeeklyRule(
            String spaceId,
            String dayOfWeek,
            String startTime,
            String endTime,
            int slotIntervalMinutes,
            int minimumBookingMinutes,
            int maximumBookingMinutes,
            boolean active) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return availabilityService.saveSpaceWeeklyRule(
                    ctx.space, dayOfWeek, startTime, endTime,
                    slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes, active, idToken);
        });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> createSpaceAvailabilityBlock(
            String spaceId,
            long startAt,
            long endAt,
            String reason) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return availabilityService.createSpaceBlock(ctx.space, startAt, endAt, reason, idToken);
        });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> cancelSpaceAvailabilityBlock(
            String spaceId,
            String blockId) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            return availabilityService.cancelSpaceBlock(spaceId, blockId, idToken);
        });
    }

    public CompletableFuture<OperationResult<List<AvailableSlot>>> previewSpaceSlots(
            String spaceId,
            LocalDate date) {

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, false, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            String dayOfWeek = date != null ? date.getDayOfWeek().name() : null;

            CompletableFuture<OperationResult<List<AvailabilityRule>>> rulesFut =
                    availabilityService.loadSpaceWeeklyRules(spaceId, idToken);

            CompletableFuture<OperationResult<List<AvailabilityBlock>>> blocksFut =
                    availabilityService.loadSpaceBlocks(spaceId, idToken);

            return rulesFut.thenCombine(blocksFut, (rulesRes, blocksRes) -> {
                if (rulesRes.isFailure()) {
                    return OperationResult.failure(rulesRes.getErrorCode(), rulesRes.getMessage());
                }
                if (blocksRes.isFailure()) {
                    return OperationResult.failure(blocksRes.getErrorCode(), blocksRes.getMessage());
                }

                List<AvailabilityRule> rules = rulesRes.getData();
                AvailabilityRule targetRule = null;
                if (rules != null && dayOfWeek != null) {
                    for (AvailabilityRule r : rules) {
                        if (r != null && dayOfWeek.equalsIgnoreCase(r.getDayOfWeek())) {
                            targetRule = r;
                            break;
                        }
                    }
                }

                return availabilityService.generateSpaceAvailableSlots(
                        ctx.space, date, targetRule, blocksRes.getData());
            });
        });
    }

    public CompletableFuture<OperationResult<List<AvailabilityRule>>> applyOperatingHoursToSpaceAvailability(String spaceId) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, idToken);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            Space space = ctx.space;

            return availabilityService.loadSpaceWeeklyRules(spaceId, idToken).thenCompose(existingRulesRes -> {
                Map<String, AvailabilityRule> existingMap = new HashMap<>();
                if (existingRulesRes.isSuccess() && existingRulesRes.getData() != null) {
                    for (AvailabilityRule r : existingRulesRes.getData()) {
                        if (r != null && r.getDayOfWeek() != null) {
                            String norm = com.eklavya.spotfit.constants.WeekDay.normalizeDay(r.getDayOfWeek());
                            if (norm != null) {
                                existingMap.put(norm, r);
                            }
                        }
                    }
                }

                List<String> canonicalDays = List.of(
                        com.eklavya.spotfit.constants.WeekDay.MONDAY,
                        com.eklavya.spotfit.constants.WeekDay.TUESDAY,
                        com.eklavya.spotfit.constants.WeekDay.WEDNESDAY,
                        com.eklavya.spotfit.constants.WeekDay.THURSDAY,
                        com.eklavya.spotfit.constants.WeekDay.FRIDAY,
                        com.eklavya.spotfit.constants.WeekDay.SATURDAY,
                        com.eklavya.spotfit.constants.WeekDay.SUNDAY
                );

                Map<String, com.eklavya.spotfit.model.OperatingDayConfig> opHours = space.getOperatingHours();
                List<String> opDaysList = space.getOperatingDays();

                int slotInterval = 60;
                int minDur = 60;
                int maxDur = 240;

                com.eklavya.spotfit.model.bookingconfig.BookingConfig config = space.getBookingConfig();
                if (config instanceof com.eklavya.spotfit.model.bookingconfig.SlotBookingConfig s) {
                    slotInterval = s.getSlotIntervalMinutes() > 0 ? s.getSlotIntervalMinutes() : 60;
                    minDur = s.getMinimumDurationMinutes() > 0 ? s.getMinimumDurationMinutes() : slotInterval;
                    maxDur = s.getMaximumDurationMinutes() >= minDur ? s.getMaximumDurationMinutes() : 240;
                } else if (config instanceof com.eklavya.spotfit.model.bookingconfig.PracticeRoomBookingConfig p) {
                    slotInterval = p.getDurationStepMinutes() > 0 ? p.getDurationStepMinutes() : 60;
                    minDur = p.getMinimumDurationMinutes() > 0 ? p.getMinimumDurationMinutes() : slotInterval;
                    maxDur = p.getMaximumDurationMinutes() >= minDur ? p.getMaximumDurationMinutes() : 240;
                } else if (config instanceof com.eklavya.spotfit.model.bookingconfig.VenueBookingConfig v) {
                    slotInterval = 60;
                    minDur = v.getMinimumBookingHours() > 0 ? v.getMinimumBookingHours() * 60 : 60;
                    maxDur = v.getMaximumBookingHours() >= v.getMinimumBookingHours() ? v.getMaximumBookingHours() * 60 : 240;
                }

                List<CompletableFuture<OperationResult<AvailabilityRule>>> futures = new ArrayList<>();
                List<String> actionDays = new ArrayList<>();

                for (String day : canonicalDays) {
                    boolean isOpen = false;
                    String openTime = "09:00";
                    String closeTime = "18:00";

                    if (opHours != null && opHours.containsKey(day)) {
                        com.eklavya.spotfit.model.OperatingDayConfig dCfg = opHours.get(day);
                        if (dCfg != null && dCfg.isActive()) {
                            isOpen = true;
                            if (dCfg.getOpeningTime() != null && !dCfg.getOpeningTime().isBlank()) openTime = dCfg.getOpeningTime();
                            if (dCfg.getClosingTime() != null && !dCfg.getClosingTime().isBlank()) closeTime = dCfg.getClosingTime();
                        }
                    } else if (opDaysList != null) {
                        for (String d : opDaysList) {
                            if (day.equals(com.eklavya.spotfit.constants.WeekDay.normalizeDay(d))) {
                                isOpen = true;
                                if (space.getOpeningTime() != null && !space.getOpeningTime().isBlank()) openTime = space.getOpeningTime();
                                if (space.getClosingTime() != null && !space.getClosingTime().isBlank()) closeTime = space.getClosingTime();
                                break;
                            }
                        }
                    }

                    AvailabilityRule existingRule = existingMap.get(day);

                    if (isOpen) {
                        actionDays.add(day);
                        futures.add(availabilityService.saveSpaceWeeklyRule(
                                space, day, openTime, closeTime, slotInterval, minDur, maxDur, true, idToken));
                    } else if (existingRule != null && existingRule.isActive()) {
                        actionDays.add(day);
                        futures.add(availabilityService.saveSpaceWeeklyRule(
                                space, day, existingRule.getStartTime(), existingRule.getEndTime(),
                                existingRule.getSlotIntervalMinutes(), existingRule.getMinimumBookingMinutes(),
                                existingRule.getMaximumBookingMinutes(), false, idToken));
                    }
                }

                if (futures.isEmpty()) {
                    return CompletableFuture.completedFuture(
                            OperationResult.success(new ArrayList<>(), "No changes required for Space operating hours."));
                }

                CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                return allOf.thenCompose(v -> {
                    List<String> failedDays = new ArrayList<>();
                    for (int i = 0; i < futures.size(); i++) {
                        try {
                            OperationResult<AvailabilityRule> res = futures.get(i).join();
                            if (res == null || res.isFailure()) {
                                String dayName = actionDays.get(i);
                                String errorMsg = (res != null) ? res.getMessage() : "Unknown error";
                                failedDays.add(dayName + " (" + errorMsg + ")");
                            }
                        } catch (Exception e) {
                            failedDays.add(actionDays.get(i) + " (" + e.getMessage() + ")");
                        }
                    }

                    if (!failedDays.isEmpty()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<List<AvailabilityRule>>failure("BULK_SAVE_PARTIAL_FAILURE",
                                        "Failed to update operating hours for: " + String.join(", ", failedDays)));
                    }

                    return availabilityService.loadSpaceWeeklyRules(spaceId, idToken)
                            .thenApply(res -> {
                                if (res.isFailure()) {
                                    return OperationResult.failure(res.getErrorCode(), res.getMessage());
                                }
                                return OperationResult.success(res.getData(), "Space operating hours applied successfully.");
                            });
                });
            });
        });
    }

    private OperationResult<SpaceAuthContext> verifySpaceOwnerAndV2Context(String spaceId, boolean isWriteOperation, String idToken) {
        if (!AppSession.isAuthenticated()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User is not authenticated.");
        }
        if (AppSession.getRole() != UserRole.OWNER) {
            return OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Space Owners can manage space availability.");
        }

        String ownerId = AppSession.getUserId();

        if (spaceId == null || spaceId.isBlank()) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required.");
        }

        try {
            OperationResult<Space> spaceRes = spaceDao.getSpace(spaceId, idToken).get();
            if (spaceRes.isFailure()) {
                return OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage());
            }
            Space space = spaceRes.getData();
            if (!ownerId.equals(space.getOwnerId())) {
                return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied. Space does not belong to authenticated Owner.");
            }
            if (space.getSchemaVersion() != 2) {
                return OperationResult.failure(OperationCode.SCHEMA_VERSION_MISMATCH, "Direct space availability is only supported for V2 Spaces.");
            }

            // Check applicable booking types
            String bookingType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
            boolean isApplicable = "SLOT".equals(bookingType) || "VENUE".equals(bookingType) || "PRACTICE_ROOM".equals(bookingType);
            if (!isApplicable) {
                return OperationResult.failure("UNSUPPORTED_BOOKING_TYPE",
                        "Direct space availability is not supported for booking type: " + bookingType);
            }

            // Enforce write lifecycle rules: write allowed only if DRAFT, REJECTED, or APPROVED
            if (isWriteOperation) {
                String status = space.getApprovalStatus() != null ? space.getApprovalStatus().toUpperCase() : "";
                if ("SUBMITTED".equals(status) || "UNDER_REVIEW".equals(status)) {
                    return OperationResult.failure(OperationCode.LIFECYCLE_WRITE_DENIED,
                            "Availability modifications are locked while Space is under review.");
                }
            }

            return OperationResult.success(new SpaceAuthContext(space, idToken));

        } catch (Exception e) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Failed to verify V2 Space ownership context.");
        }
    }

    private static class AuthContext {
        final Space space;
        final Resource resource;
        final String idToken;

        AuthContext(Space space, Resource resource, String idToken) {
            this.space = space;
            this.resource = resource;
            this.idToken = idToken;
        }
    }

    private static class SpaceAuthContext {
        final Space space;
        final String idToken;

        SpaceAuthContext(Space space, String idToken) {
            this.space = space;
            this.idToken = idToken;
        }
    }
}
