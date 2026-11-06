package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SeatSelectionMode;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.dao.SeatDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Domain service for managing Seat units in V2 SEAT Spaces.
 */
public class SeatService {

    private final SeatDao seatDao;

    public SeatService() {
        this.seatDao = new SeatDao();
    }

    public SeatService(SeatDao seatDao) {
        this.seatDao = (seatDao != null) ? seatDao : new SeatDao();
    }

    public OperationResult<Void> validateSeatSpace(Space space) {
        if (space == null) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space cannot be null.");
        }
        if (space.getSchemaVersion() != 2) {
            return OperationResult.failure(OperationCode.SCHEMA_VERSION_MISMATCH, "Seats are only supported for V2 Spaces.");
        }
        String bType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
        if (!"SEAT".equals(bType)) {
            return OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Seats can only be created for SEAT booking type Spaces.");
        }
        if (!(space.getBookingConfig() instanceof SeatBookingConfig)) {
            return OperationResult.failure("INVALID_BOOKING_CONFIG", "Space booking config must be SeatBookingConfig.");
        }
        return OperationResult.success(null);
    }

    public boolean isManualSeatSetupRequired(Space space) {
        OperationResult<Void> valRes = validateSeatSpace(space);
        if (valRes.isFailure()) return false;
        SeatBookingConfig config = (SeatBookingConfig) space.getBookingConfig();
        String mode = config.getSelectionMode() != null ? config.getSelectionMode().toUpperCase() : "";
        return !SeatSelectionMode.AUTO_ASSIGN.equals(mode);
    }

    public CompletableFuture<OperationResult<Seat>> createSeat(Space space, Seat seat, String idToken) {
        OperationResult<Void> valRes = validateSeatSpace(space);
        if (valRes.isFailure()) {
            return CompletableFuture.completedFuture(OperationResult.failure(valRes.getErrorCode(), valRes.getMessage()));
        }

        if (seat == null) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat object cannot be null."));
        }
        if (seat.getSeatName() == null || seat.getSeatName().isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat name is required."));
        }
        if (seat.getSeatCode() == null || seat.getSeatCode().isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat code is required."));
        }
        if (seat.getCapacity() <= 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat capacity must be greater than zero."));
        }
        if (seat.getPriceOverridePaise() < 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price override cannot be negative."));
        }
        if (!SeatStatus.isValid(seat.getStatus())) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid seat status: " + seat.getStatus()));
        }

        SeatBookingConfig config = (SeatBookingConfig) space.getBookingConfig();
        String mode = config.getSelectionMode() != null ? config.getSelectionMode().toUpperCase() : "";
        if (!SeatSelectionMode.TABLE_LIST.equals(mode) && seat.getCapacity() > 1) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_SEAT_CAPACITY", "Seat capacity > 1 is only allowed for TABLE_LIST selection mode."));
        }

        // Validate seat code uniqueness within Space
        return seatDao.getSeatsForSpace(space.getSpaceId(), idToken)
                .thenCompose(existingSeatsRes -> {
                    if (existingSeatsRes.isFailure()) {
                        return CompletableFuture.completedFuture(OperationResult.failure(existingSeatsRes.getErrorCode(), existingSeatsRes.getMessage()));
                    }
                    List<Seat> existingSeats = existingSeatsRes.getData();
                    if (existingSeats != null) {
                        for (Seat s : existingSeats) {
                            if (s.getSeatCode().equalsIgnoreCase(seat.getSeatCode().trim())) {
                                return CompletableFuture.completedFuture(OperationResult.failure("DUPLICATE_SEAT_CODE", "Seat code '" + seat.getSeatCode() + "' already exists in this Space."));
                            }
                        }
                    }

                    seat.setSpaceId(space.getSpaceId());
                    long now = System.currentTimeMillis();
                    seat.setCreatedAt(now);
                    seat.setUpdatedAt(now);
                    seat.setActive(true);

                    return seatDao.createSeat(space.getSpaceId(), seat, idToken);
                });
    }

    public CompletableFuture<OperationResult<Seat>> updateSeat(Space space, Seat seat, String idToken) {
        OperationResult<Void> valRes = validateSeatSpace(space);
        if (valRes.isFailure()) {
            return CompletableFuture.completedFuture(OperationResult.failure(valRes.getErrorCode(), valRes.getMessage()));
        }

        if (seat == null || seat.getSeatId() == null || seat.getSeatId().isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat and Seat ID are required."));
        }
        if (seat.getSeatName() == null || seat.getSeatName().isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat name is required."));
        }
        if (seat.getSeatCode() == null || seat.getSeatCode().isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat code is required."));
        }
        if (seat.getCapacity() <= 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat capacity must be greater than zero."));
        }
        if (seat.getPriceOverridePaise() < 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price override cannot be negative."));
        }
        if (!SeatStatus.isValid(seat.getStatus())) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid seat status: " + seat.getStatus()));
        }

        SeatBookingConfig config = (SeatBookingConfig) space.getBookingConfig();
        String mode = config.getSelectionMode() != null ? config.getSelectionMode().toUpperCase() : "";
        if (!SeatSelectionMode.TABLE_LIST.equals(mode) && seat.getCapacity() > 1) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_SEAT_CAPACITY", "Seat capacity > 1 is only allowed for TABLE_LIST selection mode."));
        }

        // Validate code uniqueness excluding self
        return seatDao.getSeatsForSpace(space.getSpaceId(), idToken)
                .thenCompose(existingSeatsRes -> {
                    if (existingSeatsRes.isFailure()) {
                        return CompletableFuture.completedFuture(OperationResult.failure(existingSeatsRes.getErrorCode(), existingSeatsRes.getMessage()));
                    }
                    List<Seat> existingSeats = existingSeatsRes.getData();
                    if (existingSeats != null) {
                        for (Seat s : existingSeats) {
                            if (!s.getSeatId().equals(seat.getSeatId()) && s.getSeatCode().equalsIgnoreCase(seat.getSeatCode().trim())) {
                                return CompletableFuture.completedFuture(OperationResult.failure("DUPLICATE_SEAT_CODE", "Seat code '" + seat.getSeatCode() + "' already exists in this Space."));
                            }
                        }
                    }

                    seat.setSpaceId(space.getSpaceId());
                    seat.setUpdatedAt(System.currentTimeMillis());

                    return seatDao.updateSeat(space.getSpaceId(), seat, idToken);
                });
    }

    public CompletableFuture<OperationResult<Seat>> getSeat(String spaceId, String seatId, String idToken) {
        return seatDao.getSeat(spaceId, seatId, idToken);
    }

    public CompletableFuture<OperationResult<List<Seat>>> getSeatsForSpace(String spaceId, String idToken) {
        return seatDao.getSeatsForSpace(spaceId, idToken);
    }

    public CompletableFuture<OperationResult<Seat>> setSeatActive(String spaceId, String seatId, boolean active, String idToken) {
        return seatDao.setSeatActive(spaceId, seatId, active, System.currentTimeMillis(), idToken);
    }
}
