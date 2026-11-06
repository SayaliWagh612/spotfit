package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.*;
import com.eklavya.spotfit.dao.*;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.util.ValidationUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Service performing application-layer validation, price recalculation in long paise,
 * and creating pre-payment booking intents (status PENDING_PAYMENT).
 */
public class BookingService {

    private final BookingDao bookingDao;
    private final SpaceDao spaceDao;
    private final BusinessDao businessDao;
    private final SeatDao seatDao;
    private final WorkshopSessionDao workshopSessionDao;
    private final AvailabilityDao availabilityDao;
    private final UserDao userDao;

    public BookingService() {
        this(new BookingDao(), new SpaceDao(), new BusinessDao(), new SeatDao(), new WorkshopSessionDao(), new AvailabilityDao(), new UserDao());
    }

    public BookingService(BookingDao bookingDao, SpaceDao spaceDao, BusinessDao businessDao) {
        this(bookingDao, spaceDao, businessDao, new SeatDao(), new WorkshopSessionDao(), new AvailabilityDao(), new UserDao());
    }

    public BookingService(BookingDao bookingDao,
                          SpaceDao spaceDao,
                          BusinessDao businessDao,
                          SeatDao seatDao,
                          WorkshopSessionDao workshopSessionDao,
                          AvailabilityDao availabilityDao,
                          UserDao userDao) {
        this.bookingDao = bookingDao != null ? bookingDao : new BookingDao();
        this.spaceDao = spaceDao != null ? spaceDao : new SpaceDao();
        this.businessDao = businessDao != null ? businessDao : new BusinessDao();
        this.seatDao = seatDao != null ? seatDao : new SeatDao();
        this.workshopSessionDao = workshopSessionDao != null ? workshopSessionDao : new WorkshopSessionDao();
        this.availabilityDao = availabilityDao != null ? availabilityDao : new AvailabilityDao();
        this.userDao = userDao != null ? userDao : new UserDao();
    }

    /**
     * Preview V2 booking intent price calculation without persisting.
     */
    public CompletableFuture<OperationResult<BookingPreviewResult>> previewV2Booking(BookingRequest request, String idToken) {
        if (request == null || ValidationUtil.isBlank(request.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return validateAndCalculateQuote(request, idToken).thenApply(res -> {
            if (res.isFailure()) {
                return OperationResult.failure(res.getErrorCode(), res.getMessage());
            }
            return OperationResult.success(res.getData());
        });
    }

    /**
     * Independently reloads space/configs from Firestore, recalculates quote, and persists pre-payment booking intent.
     */
    public CompletableFuture<OperationResult<Booking>> createV2Booking(BookingRequest request, String customerId, String idToken) {
        if (request == null || ValidationUtil.isBlank(request.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (ValidationUtil.isBlank(customerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Customer ID is required."));
        }

        return validateAndCalculateQuote(request, idToken).thenCompose(quoteRes -> {
            if (quoteRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(quoteRes.getErrorCode(), quoteRes.getMessage()));
            }

            BookingPreviewResult quote = quoteRes.getData();

            return spaceDao.getSpace(request.getSpaceId(), idToken).thenCompose(spaceRes -> {
                if (spaceRes.isFailure() || spaceRes.getData() == null) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
                }
                Space space = spaceRes.getData();

                return businessDao.getBusiness(space.getBusinessId(), idToken).thenCompose(busRes -> {
                    Business bus = busRes.getData();
                    String businessName = bus != null ? bus.getBusinessName() : "";
                    String ownerId = bus != null ? bus.getOwnerId() : space.getOwnerId();

                    return userDao.getUser(customerId, idToken).thenCompose(userRes -> {
                        User user = userRes.getData();
                        String custName = user != null ? user.getName() : "Customer";
                        String custEmail = user != null ? user.getEmail() : "";
                        String custPhone = user != null ? user.getPhone() : "";

                        long now = System.currentTimeMillis();
                        Booking booking = new Booking();
                        booking.setBookingId(UUID.randomUUID().toString());
                        booking.setSchemaVersion(2);
                        booking.setCustomerId(customerId);
                        booking.setCustomerName(custName.trim());
                        booking.setCustomerEmail(custEmail);
                        booking.setCustomerPhone(custPhone);

                        booking.setSpaceId(space.getSpaceId());
                        booking.setBusinessId(space.getBusinessId());
                        booking.setOwnerId(ownerId);
                        booking.setSpaceName(space.getName());
                        booking.setBusinessName(businessName);
                        booking.setBookingType(space.getBookingType());

                        booking.setStartAt(quote.getStartAt());
                        booking.setEndAt(quote.getEndAt());
                        booking.setParticipantCount(quote.getParticipantCountOrQuantity());
                        booking.setQuantity(quote.getParticipantCountOrQuantity());

                        booking.setSeatId(request.getSeatId());
                        booking.setSeatName(request.getSeatName());
                        booking.setSessionId(request.getSessionId());
                        booking.setDeliveryAddress(request.getDeliveryAddress());
                        booking.setRequirements(request.getRequirements());
                        booking.setRequiresApproval(quote.isRequiresApproval());

                        booking.setBaseAmountPaise(quote.getBaseQuotePaise());
                        booking.setAdditionalAmountPaise(quote.getAdditionalQuotePaise());
                        booking.setTotalAmountPaise(quote.getTotalQuotePaise());
                        booking.setQuotedTotalAmountPaise(quote.getTotalQuotePaise());
                        booking.setPricingUnit(quote.getPricingUnit());

                        // EXCLUSIVELY PENDING_PAYMENT for newly created V2 pre-payment booking intents
                        booking.setStatus(BookingStatus.PENDING_PAYMENT);
                        booking.setPaymentStatus("PENDING");
                        booking.setVerificationCode("SPF-" + Math.abs(booking.getBookingId().hashCode() % 90000 + 10000));

                        booking.setCreatedAt(now);
                        booking.setUpdatedAt(now);

                        if (request.getSelectedDate() != null) {
                            booking.setDate(request.getSelectedDate().toString());
                        } else if (quote.getStartAt() > 0) {
                            booking.setDate(Instant.ofEpochMilli(quote.getStartAt()).atZone(ZoneId.systemDefault()).toLocalDate().toString());
                        }

                        return bookingDao.createBooking(booking, idToken);
                    });
                });
            });
        });
    }

    public CompletableFuture<OperationResult<List<Booking>>> getCustomerBookings(String customerId, String idToken) {
        return bookingDao.getBookingsForCustomer(customerId, idToken);
    }

    public CompletableFuture<OperationResult<Booking>> getBooking(String bookingId, String idToken) {
        return bookingDao.getBooking(bookingId, idToken);
    }

    public CompletableFuture<OperationResult<List<Booking>>> getOwnerBookings(String ownerId, String idToken) {
        return bookingDao.getBookingsForOwner(ownerId, idToken);
    }

    public CompletableFuture<OperationResult<List<Booking>>> getManagerBookings(String spaceId, String idToken) {
        return bookingDao.getBookingsForManager(spaceId, idToken);
    }

    public CompletableFuture<OperationResult<Booking>> checkInBooking(String bookingId, String managerId, String idToken) {
        return getBooking(bookingId, idToken).thenCompose(bookingRes -> {
            if (bookingRes.isFailure() || bookingRes.getData() == null) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.NOT_FOUND, "Booking not found."));
            }
            Booking booking = bookingRes.getData();

            // Validate status transitions
            if (BookingStatus.PENDING_PAYMENT.equals(booking.getStatus()) || !PaymentStatus.isSuccessful(booking.getPaymentStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_STATE, "Booking cannot be checked in without verified payment."));
            }
            if (BookingStatus.isFinalStatus(booking.getStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_STATE, "Booking is in a final state and cannot be checked in."));
            }
            if (!BookingStatus.CONFIRMED.equals(booking.getStatus()) && !BookingStatus.CHECK_IN_PENDING.equals(booking.getStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_STATE, "Booking is not in a valid state for check-in."));
            }

            long now = System.currentTimeMillis();
            booking.setStatus(BookingStatus.CHECKED_IN);
            booking.setCheckInTime(Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toString());
            booking.setUpdatedAt(now);
            // Checked in by manager is omitted since we don't have the field yet, just update status.

            return bookingDao.updateBooking(booking, idToken);
        });
    }

    public CompletableFuture<OperationResult<Booking>> checkOutBooking(String bookingId, String managerId, String idToken) {
        return getBooking(bookingId, idToken).thenCompose(bookingRes -> {
            if (bookingRes.isFailure() || bookingRes.getData() == null) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.NOT_FOUND, "Booking not found."));
            }
            Booking booking = bookingRes.getData();

            if (!BookingStatus.CHECKED_IN.equals(booking.getStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_STATE, "Booking must be CHECKED_IN to be checked out."));
            }

            long now = System.currentTimeMillis();
            booking.setStatus(BookingStatus.COMPLETED);
            booking.setCheckOutTime(Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toString());
            booking.setUpdatedAt(now);

            return bookingDao.updateBooking(booking, idToken);
        });
    }

    private CompletableFuture<OperationResult<BookingPreviewResult>> validateAndCalculateQuote(BookingRequest request, String idToken) {
        return spaceDao.getSpace(request.getSpaceId(), idToken).thenCompose(spaceRes -> {
            if (spaceRes.isFailure() || spaceRes.getData() == null) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.NOT_FOUND, "Requested space does not exist."));
            }
            Space space = spaceRes.getData();

            // Re-validate V2 space customer visibility contract
            if (space.getSchemaVersion() != 2) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space is not a V2 schema space."));
            }
            if (!SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.PERMISSION_DENIED, "Space is not approved."));
            }
            if (!SpaceOperationalStatus.ACTIVE.equalsIgnoreCase(space.getOperationalStatus()) || !space.isActive()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.PERMISSION_DENIED, "Space is currently inactive."));
            }
            if (!SpaceCategory.CREATIVITY.equalsIgnoreCase(space.getCategory())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space category must be CREATIVITY."));
            }

            return businessDao.getBusiness(space.getBusinessId(), idToken).thenCompose(busRes -> {
                if (busRes.isFailure() || busRes.getData() == null) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NOT_FOUND, "Parent Business not found."));
                }
                Business bus = busRes.getData();

                if (bus.getSchemaVersion() != 2
                        || !BusinessStatus.APPROVED.equalsIgnoreCase(bus.getVerificationStatus())
                        || !bus.isActive()) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.PERMISSION_DENIED, "Parent Business is not eligible or active."));
                }

                String bType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
                switch (bType) {
                    case BookingType.SEAT:
                        return calculateSeatQuote(space, request, idToken);
                    case BookingType.SLOT:
                        return calculateSlotQuote(space, request, idToken);
                    case BookingType.WORKSHOP:
                        return calculateWorkshopQuote(space, request, idToken);
                    case BookingType.RENTAL:
                        return calculateRentalQuote(space, request, idToken);
                    case BookingType.VENUE:
                        return calculateVenueQuote(space, request, idToken);
                    case BookingType.PRACTICE_ROOM:
                        return calculatePracticeRoomQuote(space, request, idToken);
                    default:
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Unsupported V2 booking type: " + bType));
                }
            });
        });
    }

    private CompletableFuture<OperationResult<BookingPreviewResult>> calculateSeatQuote(Space space, BookingRequest req, String idToken) {
        SeatBookingConfig cfg = (space.getBookingConfig() instanceof SeatBookingConfig) ? (SeatBookingConfig) space.getBookingConfig() : new SeatBookingConfig();
        int seatCount = req.getSeatCount() > 0 ? req.getSeatCount() : 1;

        CompletableFuture<OperationResult<Void>> seatValidationFut;
        if ("AUTO_ASSIGN".equalsIgnoreCase(cfg.getSelectionMode())) {
            seatValidationFut = CompletableFuture.completedFuture(OperationResult.success(null));
        } else {
            if (ValidationUtil.isBlank(req.getSeatId())) {
                return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat selection is required."));
            }
            seatValidationFut = seatDao.getSeat(space.getSpaceId(), req.getSeatId(), idToken).thenApply(seatRes -> {
                if (seatRes.isFailure() || seatRes.getData() == null) {
                    return OperationResult.failure(OperationCode.NOT_FOUND, "Selected Seat not found in Space.");
                }
                Seat seat = seatRes.getData();
                if (!seat.isActive() || !SeatStatus.AVAILABLE.equalsIgnoreCase(seat.getStatus())) {
                    return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Selected Seat is unavailable.");
                }
                return OperationResult.success(null);
            });
        }

        return seatValidationFut.thenApply(valRes -> {
            if (valRes.isFailure()) {
                return OperationResult.failure(valRes.getErrorCode(), valRes.getMessage());
            }

            try {
                long basePrice = space.getBasePricePaise() > 0 ? space.getBasePricePaise() : 0L;
                int durationHours = req.getDurationHours() > 0 ? req.getDurationHours() : 1;

                long total;
                if (PricingUnit.HOUR.equalsIgnoreCase(space.getPricingUnit())) {
                    total = Math.multiplyExact(Math.multiplyExact(basePrice, durationHours), seatCount);
                } else {
                    total = Math.multiplyExact(basePrice, seatCount);
                }

                BookingPreviewResult preview = new BookingPreviewResult();
                preview.setSpaceId(space.getSpaceId());
                preview.setBookingType(BookingType.SEAT);
                preview.setBaseQuotePaise(basePrice);
                preview.setAdditionalQuotePaise(0L);
                preview.setTotalQuotePaise(total);
                preview.setPricingUnit(space.getPricingUnit() != null ? space.getPricingUnit() : PricingUnit.HOUR);
                preview.setParticipantCountOrQuantity(seatCount);

                long now = System.currentTimeMillis();
                long startMs = (req.getSelectedDate() != null) ? req.getSelectedDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() : now + 3600000L;
                preview.setStartAt(startMs);
                preview.setEndAt(startMs + (durationHours * 3600000L));

                return OperationResult.success(preview);
            } catch (ArithmeticException e) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price calculation overflow.");
            }
        });
    }

    private CompletableFuture<OperationResult<BookingPreviewResult>> calculateSlotQuote(Space space, BookingRequest req, String idToken) {
        if (req.getSelectedDate() == null || req.getStartTime() == null || req.getEndTime() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Date, start time, and end time are required."));
        }

        ZoneId zoneId = ZoneId.systemDefault();
        if (!ValidationUtil.isBlank(space.getTimeZoneId())) {
            try { zoneId = ZoneId.of(space.getTimeZoneId()); } catch (Exception ignored) {}
        }

        ZonedDateTime startZdt = ZonedDateTime.of(req.getSelectedDate(), req.getStartTime(), zoneId);
        ZonedDateTime endZdt = ZonedDateTime.of(req.getSelectedDate(), req.getEndTime(), zoneId);

        long startMs = startZdt.toInstant().toEpochMilli();
        long endMs = endZdt.toInstant().toEpochMilli();
        long nowMs = System.currentTimeMillis();

        if (startMs <= nowMs) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Requested time must be in the future."));
        }
        if (endMs <= startMs) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "End time must be after start time."));
        }

        return availabilityDao.getSpaceAvailabilityBlocks(space.getSpaceId(), idToken).thenApply(blocksRes -> {
            if (blocksRes.isSuccess() && blocksRes.getData() != null) {
                for (AvailabilityBlock block : blocksRes.getData()) {
                    if (block != null && block.isActive() && "ACTIVE".equalsIgnoreCase(block.getStatus())) {
                        if (startMs < block.getEndAt() && endMs > block.getStartAt()) {
                            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Selected time overlaps an active availability block.");
                        }
                    }
                }
            }

            try {
                long durationMinutes = (endMs - startMs) / 60000L;
                long basePrice = space.getBasePricePaise() > 0 ? space.getBasePricePaise() : 0L;
                long hours = (durationMinutes + 59) / 60;
                long total = Math.multiplyExact(basePrice, Math.max(1, hours));

                BookingPreviewResult preview = new BookingPreviewResult();
                preview.setSpaceId(space.getSpaceId());
                preview.setBookingType(space.getBookingType());
                preview.setStartAt(startMs);
                preview.setEndAt(endMs);
                preview.setBaseQuotePaise(basePrice);
                preview.setAdditionalQuotePaise(0L);
                preview.setTotalQuotePaise(total);
                preview.setPricingUnit(space.getPricingUnit() != null ? space.getPricingUnit() : PricingUnit.HOUR);
                preview.setParticipantCountOrQuantity(1);

                return OperationResult.success(preview);
            } catch (ArithmeticException e) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price calculation overflow.");
            }
        });
    }

    private CompletableFuture<OperationResult<BookingPreviewResult>> calculateWorkshopQuote(Space space, BookingRequest req, String idToken) {
        if (ValidationUtil.isBlank(req.getSessionId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Workshop session selection is required."));
        }

        int requestedParticipants = req.getParticipantCount() > 0 ? req.getParticipantCount() : 1;

        return workshopSessionDao.getSession(space.getSpaceId(), req.getSessionId(), idToken).thenApply(sessRes -> {
            if (sessRes.isFailure() || sessRes.getData() == null) {
                return OperationResult.failure(OperationCode.NOT_FOUND, "Selected Workshop Session not found.");
            }
            WorkshopSession session = sessRes.getData();

            if (!session.isActive() || !WorkshopSessionStatus.SCHEDULED.equalsIgnoreCase(session.getStatus())) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Workshop Session is not active or scheduled.");
            }
            if (session.getStartAt() <= System.currentTimeMillis()) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Workshop Session has already started or completed.");
            }
            WorkshopBookingConfig wCfg = (space.getBookingConfig() instanceof WorkshopBookingConfig) 
                    ? (WorkshopBookingConfig) space.getBookingConfig() : new WorkshopBookingConfig();

            if (requestedParticipants < wCfg.getMinimumParticipantsPerBooking()) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Requested participants below minimum allowed.");
            }
            if (requestedParticipants > wCfg.getMaximumParticipantsPerBooking()) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Requested participants above maximum allowed.");
            }
            if (session.getBookedCount() + requestedParticipants > session.getCapacity()) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Workshop Session capacity exceeded.");
            }

            try {
                long perPersonPrice = session.getPriceOverridePaise() > 0 ? session.getPriceOverridePaise() : wCfg.getPricePerPersonPaise();
                long total = Math.multiplyExact(perPersonPrice, requestedParticipants);

                BookingPreviewResult preview = new BookingPreviewResult();
                preview.setSpaceId(space.getSpaceId());
                preview.setBookingType(BookingType.WORKSHOP);
                preview.setStartAt(session.getStartAt());
                preview.setEndAt(session.getEndAt());
                preview.setParticipantCountOrQuantity(requestedParticipants);
                preview.setBaseQuotePaise(perPersonPrice);
                preview.setAdditionalQuotePaise(0L);
                preview.setTotalQuotePaise(total);
                preview.setPricingUnit(PricingUnit.PERSON);

                return OperationResult.success(preview);
            } catch (ArithmeticException e) {
                return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price calculation overflow.");
            }
        });
    }

    private CompletableFuture<OperationResult<BookingPreviewResult>> calculateRentalQuote(Space space, BookingRequest req, String idToken) {
        int rentalDays = req.getRentalDays() > 0 ? req.getRentalDays() : 1;
        int quantity = req.getQuantity() > 0 ? req.getQuantity() : 1;

        try {
            long basePrice = space.getBasePricePaise() > 0 ? space.getBasePricePaise() : 0L;
            long total = Math.multiplyExact(Math.multiplyExact(basePrice, rentalDays), quantity);

            BookingPreviewResult preview = new BookingPreviewResult();
            preview.setSpaceId(space.getSpaceId());
            preview.setBookingType(BookingType.RENTAL);
            preview.setBaseQuotePaise(basePrice);
            preview.setAdditionalQuotePaise(0L);
            preview.setTotalQuotePaise(total);
            preview.setPricingUnit(PricingUnit.DAY);
            preview.setParticipantCountOrQuantity(quantity);
            preview.setPrototypeLimitationMessage("Final stock availability will be verified before payment.");

            long now = System.currentTimeMillis();
            long startMs = (req.getSelectedDate() != null) ? req.getSelectedDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() : now + 86400000L;
            preview.setStartAt(startMs);
            preview.setEndAt(startMs + (rentalDays * 86400000L));

            return CompletableFuture.completedFuture(OperationResult.success(preview));
        } catch (ArithmeticException e) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price calculation overflow."));
        }
    }

    private CompletableFuture<OperationResult<BookingPreviewResult>> calculateVenueQuote(Space space, BookingRequest req, String idToken) {
        VenueBookingConfig venueCfg = (space.getBookingConfig() instanceof VenueBookingConfig) ? (VenueBookingConfig) space.getBookingConfig() : new VenueBookingConfig();
        return calculateSlotQuote(space, req, idToken).thenApply(res -> {
            if (res.isSuccess() && res.getData() != null) {
                res.getData().setRequiresApproval(venueCfg.isRequiresApproval());
                res.getData().setParticipantCountOrQuantity(req.getGuestCount() > 0 ? req.getGuestCount() : 1);
            }
            return res;
        });
    }

    private CompletableFuture<OperationResult<BookingPreviewResult>> calculatePracticeRoomQuote(Space space, BookingRequest req, String idToken) {
        return calculateSlotQuote(space, req, idToken);
    }
}
