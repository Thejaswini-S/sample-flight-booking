package com.thejaswini.flightbooking.dto;

import com.thejaswini.flightbooking.constant.ValidationConstraints;
import com.thejaswini.flightbooking.constant.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body used to book seats on a known flight. Every field is validated at the web layer
 * (via {@code @Valid}) so malformed input is rejected with 400 <em>before</em> reaching the service.
 *
 * @param flightNumber  flight to book (required; 2-10 letters/digits/hyphens)
 * @param passengerName name the booking is under (required; max 100 chars)
 * @param seats         number of seats to reserve (1..50)
 */
public record BookingRequest(

        @Schema(example = "AI-101", description = "Flight number to book")
        @NotBlank(message = ValidationMessages.REQ_FLIGHT_NUMBER)
        @Pattern(regexp = ValidationConstraints.FLIGHT_NUMBER_REGEX,
                message = ValidationMessages.REQ_FLIGHT_NUMBER_FORMAT)
        String flightNumber,

        @Schema(example = "Thejaswini S", description = "Passenger name")
        @NotBlank(message = ValidationMessages.REQ_PASSENGER_NAME)
        @Size(max = ValidationConstraints.PASSENGER_NAME_MAX, message = ValidationMessages.REQ_PASSENGER_NAME_SIZE)
        String passengerName,

        @Schema(example = "2", description = "Number of seats to reserve")
        @Min(value = 1, message = ValidationMessages.REQ_SEATS_MIN)
        @Max(value = ValidationConstraints.SEATS_MAX, message = ValidationMessages.REQ_SEATS_MAX)
        int seats) {

    /**
     * @return a new fluent {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link BookingRequest} (handy for tests and programmatic construction).
     */
    public static final class Builder {

        private String flightNumber;
        private String passengerName;
        private int seats;

        /**
         * @param flightNumber the flight number to book
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
         * @param seats the number of seats to reserve
         * @return this builder
         */
        public Builder seats(int seats) {
            this.seats = seats;
            return this;
        }

        /**
         * @return the constructed {@link BookingRequest}
         */
        public BookingRequest build() {
            return new BookingRequest(flightNumber, passengerName, seats);
        }
    }
}
