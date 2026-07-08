package com.thejaswini.flightbooking.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * API representation of a confirmed booking.
 *
 * <p>Immutable value object constructed via its {@link Builder} (Builder pattern), which the
 * generated MapStruct mapper also uses.
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
     * @return a new fluent {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link BookingResponse} (also used by the generated MapStruct mapper).
     */
    public static final class Builder {

        private UUID bookingId;
        private String flightNumber;
        private String passengerName;
        private int seats;
        private Instant createdAt;

        /**
         * @param bookingId the server-assigned booking id
         * @return this builder
         */
        public Builder bookingId(UUID bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        /**
         * @param flightNumber the booked flight number
         * @return this builder
         */
        public Builder flightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
            return this;
        }

        /**
         * @param passengerName the passenger name
         * @return this builder
         */
        public Builder passengerName(String passengerName) {
            this.passengerName = passengerName;
            return this;
        }

        /**
         * @param seats the number of seats reserved
         * @return this builder
         */
        public Builder seats(int seats) {
            this.seats = seats;
            return this;
        }

        /**
         * @param createdAt when the booking was confirmed
         * @return this builder
         */
        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return the constructed immutable {@link BookingResponse}
         */
        public BookingResponse build() {
            return new BookingResponse(bookingId, flightNumber, passengerName, seats, createdAt);
        }
    }
}
