package com.thejaswini.flightbooking.model;

import com.thejaswini.flightbooking.constant.ValidationMessages;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * An immutable, confirmed booking record. A booking exists only once seats have been
 * successfully reserved on the flight, so its very existence means it is confirmed.
 */
public record Booking(
        UUID id,
        String flightNumber,
        String passengerName,
        int seats,
        Instant createdAt) {

    /**
     * Canonical constructor enforcing the booking invariants.
     *
     * @throws NullPointerException     if any reference component is {@code null}
     * @throws IllegalArgumentException if {@code seats} is not positive
     */
    public Booking {
        Objects.requireNonNull(id, ValidationMessages.BOOKING_ID_REQUIRED);
        Objects.requireNonNull(flightNumber, ValidationMessages.FLIGHT_NUMBER_REQUIRED);
        Objects.requireNonNull(passengerName, ValidationMessages.PASSENGER_NAME_REQUIRED);
        Objects.requireNonNull(createdAt, ValidationMessages.CREATED_AT_REQUIRED);
        if (seats <= 0) {
            throw new IllegalArgumentException(ValidationMessages.BOOKING_SEATS_POSITIVE);
        }
    }
}
