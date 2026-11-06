package com.eklavya.spotfit.controller.user;

import com.eklavya.spotfit.constants.*;
import com.eklavya.spotfit.dao.*;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for Customer V2 marketplace operations without JavaFX
 * dependencies.
 */
public class CustomerMarketplaceController {

    private final SpaceService spaceService;
    private final SeatDao seatDao;
    private final WorkshopSessionDao workshopSessionDao;
    private final AvailabilityDao availabilityDao;
    private final UserDao userDao;
    private final com.eklavya.spotfit.service.BookingService bookingService;

    public CustomerMarketplaceController() {
        this(new SpaceService(), new SeatDao(), new WorkshopSessionDao(), new AvailabilityDao(), new UserDao(),
                new com.eklavya.spotfit.service.BookingService());
    }

    public CustomerMarketplaceController(SpaceService spaceService) {
        this(spaceService, new SeatDao(), new WorkshopSessionDao(), new AvailabilityDao(), new UserDao(),
                new com.eklavya.spotfit.service.BookingService());
    }

    public CustomerMarketplaceController(SpaceService spaceService,
            SeatDao seatDao,
            WorkshopSessionDao workshopSessionDao,
            AvailabilityDao availabilityDao,
            UserDao userDao) {
        this(spaceService, seatDao, workshopSessionDao, availabilityDao, userDao,
                new com.eklavya.spotfit.service.BookingService());
    }

    public CustomerMarketplaceController(SpaceService spaceService,
            SeatDao seatDao,
            WorkshopSessionDao workshopSessionDao,
            AvailabilityDao availabilityDao,
            UserDao userDao,
            com.eklavya.spotfit.service.BookingService bookingService) {
        this.spaceService = spaceService != null ? spaceService : new SpaceService();
        this.seatDao = seatDao != null ? seatDao : new SeatDao();
        this.workshopSessionDao = workshopSessionDao != null ? workshopSessionDao : new WorkshopSessionDao();
        this.availabilityDao = availabilityDao != null ? availabilityDao : new AvailabilityDao();
        this.userDao = userDao != null ? userDao : new UserDao();
        this.bookingService = bookingService != null ? bookingService
                : new com.eklavya.spotfit.service.BookingService();
    }

    private CompletableFuture<OperationResult<Void>> validateCustomerSession() {
        if (!AppSession.isAuthenticated() || ValidationUtil.isBlank(AppSession.getUserId())
                || ValidationUtil.isBlank(AppSession.getIdToken())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED,
                            "User authentication is required to access the marketplace."));
        }

        if (!UserRole.USER.equalsIgnoreCase(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH,
                            "Only Customer accounts can access the customer marketplace."));
        }

        CompletableFuture<OperationResult<Void>> future = new CompletableFuture<>();
        userDao.getUser(AppSession.getUserId(), AppSession.getIdToken())
                .thenAccept(res -> {
                    if (res.isSuccess() && res.getData() != null) {
                        User user = res.getData();
                        if (!user.isActive()) {
                            future.complete(OperationResult.failure(OperationCode.PERMISSION_DENIED,
                                    "Customer account is inactive."));
                            return;
                        }
                    }
                    future.complete(OperationResult.success(null));
                })
                .exceptionally(ex -> {
                    future.complete(OperationResult.success(null));
                    return null;
                });
        return future;
    }

    private boolean isGuest() {
        return !AppSession.isAuthenticated() || ValidationUtil.isBlank(AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<List<Space>>> searchVisibleMarketplaceSpaces(String query) {
        if (query == null || query.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Search query cannot be blank."));
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").replace("-", " ")
                .replace("_", " ");

        if (isGuest()) {
            CompletableFuture<OperationResult<List<Space>>> artFut = spaceService
                    .getPublicVisibleV2CreativitySpaces("Art", null);
            CompletableFuture<OperationResult<List<Space>>> musicFut = spaceService
                    .getPublicVisibleV2CreativitySpaces("Music", null);
            CompletableFuture<OperationResult<List<Space>>> podcastsFut = spaceService
                    .getPublicVisibleV2CreativitySpaces("Podcasts", null);
            CompletableFuture<OperationResult<List<Space>>> contentFut = spaceService
                    .getPublicVisibleV2CreativitySpaces("Content Creation", null);
            return handleSearchFutures(normalizedQuery, artFut, musicFut, podcastsFut, contentFut);
        }

        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }

            String idToken = AppSession.getIdToken();
            CompletableFuture<OperationResult<List<Space>>> artFut = spaceService.getVisibleV2CreativitySpaces("Art",
                    null, idToken);
            CompletableFuture<OperationResult<List<Space>>> musicFut = spaceService
                    .getVisibleV2CreativitySpaces("Music", null, idToken);
            CompletableFuture<OperationResult<List<Space>>> podcastsFut = spaceService
                    .getVisibleV2CreativitySpaces("Podcasts", null, idToken);
            CompletableFuture<OperationResult<List<Space>>> contentFut = spaceService
                    .getVisibleV2CreativitySpaces("Content Creation", null, idToken);

            return handleSearchFutures(normalizedQuery, artFut, musicFut, podcastsFut, contentFut);
        });
    }

    private CompletableFuture<OperationResult<List<Space>>> handleSearchFutures(String normalizedQuery,
            CompletableFuture<OperationResult<List<Space>>> artFut,
            CompletableFuture<OperationResult<List<Space>>> musicFut,
            CompletableFuture<OperationResult<List<Space>>> podcastsFut,
            CompletableFuture<OperationResult<List<Space>>> contentFut) {
        return CompletableFuture.allOf(artFut, musicFut, podcastsFut, contentFut).thenApply(v -> {
            List<Space> rawSpaces = new ArrayList<>();
            boolean anySuccess = false;
            String firstErrorCode = OperationCode.INTERNAL_ERROR;
            String firstErrorMsg = "Failed to load spaces";

            for (CompletableFuture<OperationResult<List<Space>>> fut : Arrays.asList(artFut, musicFut, podcastsFut,
                    contentFut)) {
                OperationResult<List<Space>> res = fut.join();
                if (res.isSuccess() && res.getData() != null) {
                    rawSpaces.addAll(res.getData());
                    anySuccess = true;
                } else if (res.isFailure() && !anySuccess) {
                    firstErrorCode = res.getErrorCode();
                    firstErrorMsg = res.getMessage();
                }
            }

            if (!anySuccess) {
                return OperationResult.failure(firstErrorCode, firstErrorMsg);
            }

            Map<String, Space> uniqueSpaces = new HashMap<>();
            for (Space s : rawSpaces) {
                if (s.getSpaceId() != null && !s.getSpaceId().trim().isEmpty()) {
                    uniqueSpaces.put(s.getSpaceId(), s);
                }
            }

            List<Space> matchedSpaces = new ArrayList<>();
            for (Space s : uniqueSpaces.values()) {
                boolean matches = false;
                String sName = s.getSpaceName() != null ? s.getSpaceName().toLowerCase(Locale.ROOT) : "";
                String type = s.getActivityType() != null ? s.getActivityType().toLowerCase(Locale.ROOT) : "";
                String group = s.getActivityGroup() != null ? s.getActivityGroup().toLowerCase(Locale.ROOT) : "";
                String bType = s.getBookingType() != null ? s.getBookingType().toLowerCase(Locale.ROOT) : "";
                String desc = s.getDescription() != null ? s.getDescription().toLowerCase(Locale.ROOT) : "";
                String city = s.getSpaceLocation() != null && s.getSpaceLocation().getCity() != null
                        ? s.getSpaceLocation().getCity().toLowerCase(Locale.ROOT)
                        : "";
                String area = s.getSpaceLocation() != null && s.getSpaceLocation().getAddressLine() != null
                        ? s.getSpaceLocation().getAddressLine().toLowerCase(Locale.ROOT)
                        : "";
                String amenities = s.getAmenities() != null
                        ? String.join(" ", s.getAmenities()).toLowerCase(Locale.ROOT)
                        : "";
                String facilities = s.getSuitableActivities() != null
                        ? String.join(" ", s.getSuitableActivities()).toLowerCase(Locale.ROOT)
                        : "";

                if (sName.contains(normalizedQuery) || type.contains(normalizedQuery) || group.contains(normalizedQuery)
                        ||
                        bType.contains(normalizedQuery) || desc.contains(normalizedQuery)
                        || city.contains(normalizedQuery) ||
                        area.contains(normalizedQuery) || amenities.contains(normalizedQuery)
                        || facilities.contains(normalizedQuery)) {
                    matchedSpaces.add(s);
                }
            }

            matchedSpaces.sort((s1, s2) -> {
                String n1 = s1.getSpaceName() != null ? s1.getSpaceName().toLowerCase(Locale.ROOT) : "";
                String n2 = s2.getSpaceName() != null ? s2.getSpaceName().toLowerCase(Locale.ROOT) : "";
                String t1 = s1.getActivityType() != null ? s1.getActivityType().toLowerCase(Locale.ROOT) : "";
                String t2 = s2.getActivityType() != null ? s2.getActivityType().toLowerCase(Locale.ROOT) : "";

                int score1 = 0;
                if (n1.equals(normalizedQuery))
                    score1 = 100;
                else if (n1.startsWith(normalizedQuery))
                    score1 = 80;
                else if (n1.contains(normalizedQuery))
                    score1 = 60;
                else if (t1.equals(normalizedQuery))
                    score1 = 50;
                else if (t1.contains(normalizedQuery))
                    score1 = 40;
                else
                    score1 = 10;

                int score2 = 0;
                if (n2.equals(normalizedQuery))
                    score2 = 100;
                else if (n2.startsWith(normalizedQuery))
                    score2 = 80;
                else if (n2.contains(normalizedQuery))
                    score2 = 60;
                else if (t2.equals(normalizedQuery))
                    score2 = 50;
                else if (t2.contains(normalizedQuery))
                    score2 = 40;
                else
                    score2 = 10;

                if (score1 != score2) {
                    return Integer.compare(score2, score1);
                }
                return n1.compareTo(n2);
            });

            return OperationResult.success(matchedSpaces);
        });
    }

    public CompletableFuture<OperationResult<List<Space>>> loadVisibleV2CreativitySpaces(String navigationGroupValue,
            String activityName) {
        String mappedActivityGroup = mapNavigationGroupToActivityGroup(navigationGroupValue);
        if (mappedActivityGroup == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Valid controlled Creativity activity group is required."));
        }

        if (isGuest()) {
            return spaceService.getPublicVisibleV2CreativitySpaces(mappedActivityGroup, activityName);
        }

        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }

            return spaceService.getVisibleV2CreativitySpaces(mappedActivityGroup, activityName,
                    AppSession.getIdToken());
        });
    }

    public CompletableFuture<OperationResult<List<Space>>> loadLiveCreativitySpaces(String navigationGroupValue) {
        String mappedActivityGroup = mapNavigationGroupToActivityGroup(navigationGroupValue);
        if (mappedActivityGroup == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Valid controlled Creativity activity group is required."));
        }

        if (isGuest()) {
            return spaceService.getPublicLiveCreativitySpaces(mappedActivityGroup);
        }

        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }

            return spaceService.getLiveCreativitySpaces(mappedActivityGroup, AppSession.getIdToken());
        });
    }

    public CompletableFuture<OperationResult<Space>> loadVisibleV2SpaceDetails(String spaceId) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        if (isGuest()) {
            return spaceService.getPublicVisibleV2SpaceDetails(spaceId);
        }

        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }

            return spaceService.getVisibleV2SpaceDetails(spaceId, AppSession.getIdToken());
        });
    }

    public CompletableFuture<OperationResult<CustomerBookingOptions>> loadCustomerBookingOptions(String spaceId,
            LocalDate selectedDate) {
        final LocalDate targetDate = (selectedDate != null) ? selectedDate : LocalDate.now();

        return loadVisibleV2SpaceDetails(spaceId).thenCompose(spaceRes -> {
            if (spaceRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage()));
            }

            Space space = spaceRes.getData();
            CustomerBookingOptions options = new CustomerBookingOptions(spaceId, space.getBookingType(), targetDate);
            options.setBookingConfig(space.getBookingConfig());

            String bookingType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
            String idToken = AppSession.getIdToken();

            switch (bookingType) {
                case BookingType.SEAT:
                    return loadSeatOptions(space, options, idToken);

                case BookingType.SLOT:
                case BookingType.VENUE:
                case BookingType.PRACTICE_ROOM:
                    return loadSlotOptions(space, options, targetDate, idToken);

                case BookingType.WORKSHOP:
                    return loadWorkshopOptions(space, options, idToken);

                case BookingType.RENTAL:
                    return CompletableFuture.completedFuture(OperationResult.success(options));

                default:
                    return CompletableFuture.completedFuture(OperationResult.success(options));
            }
        });
    }

    private CompletableFuture<OperationResult<CustomerBookingOptions>> loadSeatOptions(Space space,
            CustomerBookingOptions options, String idToken) {
        BookingConfig cfg = space.getBookingConfig();
        if (cfg instanceof SeatBookingConfig seatCfg) {
            String mode = seatCfg.getSelectionMode();
            if ("AUTO_ASSIGN".equalsIgnoreCase(mode)) {
                options.setSeatSelectionMessage("A seat will be assigned automatically during booking.");
                return CompletableFuture.completedFuture(OperationResult.success(options));
            }
        }

        return seatDao.getSeatsForSpace(space.getSpaceId(), idToken).thenApply(res -> {
            if (res.isFailure()) {
                return OperationResult.failure(res.getErrorCode(), res.getMessage());
            }
            List<Seat> availableSeats = new ArrayList<>();
            if (res.getData() != null) {
                for (Seat seat : res.getData()) {
                    if (seat != null && seat.isActive() && SeatStatus.AVAILABLE.equalsIgnoreCase(seat.getStatus())) {
                        availableSeats.add(seat);
                    }
                }
            }
            options.setSeats(availableSeats);
            return OperationResult.success(options);
        });
    }

    private CompletableFuture<OperationResult<CustomerBookingOptions>> loadWorkshopOptions(Space space,
            CustomerBookingOptions options, String idToken) {

        CompletableFuture<OperationResult<List<WorkshopSession>>> fetchFut;
        if (isGuest()) {
            fetchFut = workshopSessionDao.getPublicSessionsForSpace(space.getSpaceId());
        } else {
            fetchFut = workshopSessionDao.getSessionsForSpace(space.getSpaceId(), idToken);
        }

        return fetchFut.thenApply(res -> {
            if (res.isFailure()) {
                return OperationResult.failure(res.getErrorCode(), res.getMessage());
            }
            List<WorkshopSession> sessions = new ArrayList<>();
            long now = System.currentTimeMillis();
            if (res.getData() != null) {
                for (WorkshopSession session : res.getData()) {
                    if (session != null
                            && session.isActive()
                            && WorkshopSessionStatus.SCHEDULED.equalsIgnoreCase(session.getStatus())
                            && session.getStartAt() > now
                            && (session.getCapacity() - session.getBookedCount()) > 0) {
                        sessions.add(session);
                    }
                }
            }
            options.setWorkshopSessions(sessions);
            return OperationResult.success(options);
        });
    }

    private CompletableFuture<OperationResult<CustomerBookingOptions>> loadSlotOptions(Space space,
            CustomerBookingOptions options, LocalDate targetDate, String idToken) {
        CompletableFuture<OperationResult<List<AvailabilityRule>>> rulesFut = availabilityDao
                .getSpaceAvailabilityRules(space.getSpaceId(), idToken);
        CompletableFuture<OperationResult<List<AvailabilityBlock>>> blocksFut = availabilityDao
                .getSpaceAvailabilityBlocks(space.getSpaceId(), idToken);

        return CompletableFuture.allOf(rulesFut, blocksFut).thenApply(v -> {
            OperationResult<List<AvailabilityRule>> rulesRes = rulesFut.join();
            OperationResult<List<AvailabilityBlock>> blocksRes = blocksFut.join();

            if (rulesRes.isFailure()) {
                return OperationResult.failure(rulesRes.getErrorCode(), rulesRes.getMessage());
            }
            if (blocksRes.isFailure()) {
                return OperationResult.failure(blocksRes.getErrorCode(), blocksRes.getMessage());
            }

            List<AvailabilityRule> rules = rulesRes.getData() != null ? rulesRes.getData() : Collections.emptyList();
            List<AvailabilityBlock> blocks = blocksRes.getData() != null ? blocksRes.getData()
                    : Collections.emptyList();

            String dayOfWeekName = targetDate.getDayOfWeek().name();
            AvailabilityRule matchingRule = null;
            for (AvailabilityRule rule : rules) {
                if (rule != null && rule.isActive() && dayOfWeekName.equalsIgnoreCase(rule.getDayOfWeek())) {
                    matchingRule = rule;
                    break;
                }
            }

            if (matchingRule == null || ValidationUtil.isBlank(matchingRule.getStartTime())
                    || ValidationUtil.isBlank(matchingRule.getEndTime())) {
                options.setAvailableSlots(Collections.emptyList());
                return OperationResult.success(options);
            }

            ZoneId zoneId = ZoneId.systemDefault();
            if (!ValidationUtil.isBlank(space.getTimeZoneId())) {
                try {
                    zoneId = ZoneId.of(space.getTimeZoneId());
                } catch (Exception ignored) {
                }
            }

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startLT = LocalTime.parse(matchingRule.getStartTime(), timeFormatter);
            LocalTime endLT = LocalTime.parse(matchingRule.getEndTime(), timeFormatter);

            int intervalMinutes = matchingRule.getSlotIntervalMinutes() > 0 ? matchingRule.getSlotIntervalMinutes()
                    : 60;
            List<AvailableSlot> computedSlots = new ArrayList<>();

            LocalTime currentLT = startLT;
            long nowMs = System.currentTimeMillis();

            while (currentLT.plusMinutes(intervalMinutes).isBefore(endLT)
                    || currentLT.plusMinutes(intervalMinutes).equals(endLT)) {
                LocalTime nextLT = currentLT.plusMinutes(intervalMinutes);

                ZonedDateTime startZdt = ZonedDateTime.of(targetDate, currentLT, zoneId);
                ZonedDateTime endZdt = ZonedDateTime.of(targetDate, nextLT, zoneId);

                long startMs = startZdt.toInstant().toEpochMilli();
                long endMs = endZdt.toInstant().toEpochMilli();

                boolean isBlocked = false;
                if (startMs < nowMs && targetDate.equals(LocalDate.now(zoneId))) {
                    isBlocked = true;
                }

                if (!isBlocked) {
                    for (AvailabilityBlock block : blocks) {
                        if (block != null && block.isActive() && "ACTIVE".equalsIgnoreCase(block.getStatus())) {
                            if (startMs < block.getEndAt() && endMs > block.getStartAt()) {
                                isBlocked = true;
                                break;
                            }
                        }
                    }
                }

                if (!isBlocked) {
                    AvailableSlot slot = new AvailableSlot(space.getSpaceId(), "", startMs, endMs, currentLT.toString(),
                            nextLT.toString(), true);
                    computedSlots.add(slot);
                }

                currentLT = nextLT;
            }

            options.setAvailableSlots(computedSlots);
            return OperationResult.success(options);
        });
    }

    public String mapNavigationGroupToActivityGroup(String navigationGroupValue) {
        if (ValidationUtil.isBlank(navigationGroupValue)) {
            return null;
        }

        String trimmed = navigationGroupValue.trim();
        if ("Art".equalsIgnoreCase(trimmed) || CreativityActivityGroup.ART.equalsIgnoreCase(trimmed)) {
            return CreativityActivityGroup.ART;
        } else if ("Content Creation".equalsIgnoreCase(trimmed)
                || CreativityActivityGroup.CONTENT_CREATION.equalsIgnoreCase(trimmed)) {
            return CreativityActivityGroup.CONTENT_CREATION;
        } else if ("Podcasts".equalsIgnoreCase(trimmed) || CreativityActivityGroup.PODCASTS.equalsIgnoreCase(trimmed)) {
            return CreativityActivityGroup.PODCASTS;
        } else if ("Music".equalsIgnoreCase(trimmed) || CreativityActivityGroup.MUSIC.equalsIgnoreCase(trimmed)) {
            return CreativityActivityGroup.MUSIC;
        }

        return null;
    }

    public CompletableFuture<OperationResult<BookingPreviewResult>> previewV2Booking(BookingRequest request) {
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            return bookingService.previewV2Booking(request, AppSession.getIdToken());
        });
    }

    public CompletableFuture<OperationResult<Booking>> createV2Booking(BookingRequest request) {
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            return bookingService.createV2Booking(request, AppSession.getUserId(), AppSession.getIdToken());
        });
    }

    public CompletableFuture<OperationResult<List<Booking>>> loadCurrentCustomerBookings() {
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            return bookingService.getCustomerBookings(AppSession.getUserId(), AppSession.getIdToken());
        });
    }

    public CompletableFuture<OperationResult<Booking>> loadCurrentCustomerBooking(String bookingId) {
        return validateCustomerSession().thenCompose(sessionRes -> {
            if (sessionRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(sessionRes.getErrorCode(), sessionRes.getMessage()));
            }
            if (ValidationUtil.isBlank(bookingId)) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Booking ID is required."));
            }
            return bookingService.getBooking(bookingId, AppSession.getIdToken());
        });
    }
}
