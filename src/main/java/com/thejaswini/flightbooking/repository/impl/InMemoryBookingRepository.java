package com.thejaswini.flightbooking.repository.impl;

import com.thejaswini.flightbooking.constant.ValidationMessages;
import com.thejaswini.flightbooking.model.Booking;
import com.thejaswini.flightbooking.repository.BookingRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory {@link BookingRepository} backed by a {@link ConcurrentHashMap} keyed by booking id.
 */
@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final ConcurrentMap<UUID, Booking> bookings = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Booking save(Booking booking) {
        Objects.requireNonNull(booking, ValidationMessages.BOOKING_REQUIRED);
        bookings.put(booking.id(), booking);
        return booking;
    }
}
