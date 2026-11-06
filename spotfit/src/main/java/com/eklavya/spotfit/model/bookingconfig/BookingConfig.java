package com.eklavya.spotfit.model.bookingconfig;

/**
 * Marker interface for all SpotFit V2 type-safe booking configuration models.
 */
public interface BookingConfig {

    /**
     * Returns the controlled booking type constant associated with this configuration.
     * @return controlled constant from {@link com.eklavya.spotfit.constants.BookingType}
     */
    String getBookingType();

    /**
     * Creates a deep copy of this configuration instance.
     * @return a new, independent {@link BookingConfig} copy
     */
    BookingConfig copy();
}
