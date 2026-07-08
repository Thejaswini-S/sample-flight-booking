package com.thejaswini.flightbooking.repository;

import com.thejaswini.flightbooking.model.Booking;

/**
 * Storage abstraction for confirmed {@link Booking} records. Bookings are persisted so the
 * system keeps a durable record of what was sold, even though no read API is exposed (per spec).
 */
public interface BookingRepository {

    /**
     * Stores the given confirmed booking, keyed by its identifier.
     *
     * @param booking the booking to persist (must not be {@code null})
     * @return the stored booking instance
     */
    Booking save(Booking booking);
}
