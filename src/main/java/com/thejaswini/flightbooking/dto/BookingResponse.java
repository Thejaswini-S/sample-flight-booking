package com.thejaswini.flightbooking.dto;

import com.thejaswini.flightbooking.model.Booking;

import java.time.Instant;
import java.util.UUID;

/**
 * API representation of a confirmed booking.
 *
 * @param bookingId     server-assigned unique booking id
 * @param flightNumber  booked flight
 * @param passengerName passenger name
 * @param seats         seats reserved
 * @param createdAt     when the booking was confirmed
 */
public record BookingResponse(
        UUID bookingId,
        String flightNumber,
        String passengerName,
        int seats,
        Instant createdAt) {

    /**
     * Maps a {@link Booking} domain object to its API representation.
     *
     * @param booking the domain booking (must not be {@code null})
     * @return the response DTO
     */
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.id(), booking.flightNumber(),
                booking.passengerName(), booking.seats(), booking.createdAt());
    }
}
