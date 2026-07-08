package com.thejaswini.flightbooking.dto;

import com.thejaswini.flightbooking.constant.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body used to book seats on a known flight.
 *
 * @param flightNumber  flight to book (required; the client already knows it)
 * @param passengerName name the booking is under (required)
 * @param seats         number of seats to reserve (at least 1)
 */
public record BookingRequest(

        @Schema(example = "AI-101", description = "Flight number to book")
        @NotBlank(message = ValidationMessages.REQ_FLIGHT_NUMBER)
        String flightNumber,

        @Schema(example = "Thejaswini S", description = "Passenger name")
        @NotBlank(message = ValidationMessages.REQ_PASSENGER_NAME)
        String passengerName,

        @Schema(example = "2", description = "Number of seats to reserve")
        @Min(value = 1, message = ValidationMessages.REQ_SEATS_MIN)
        int seats) {
}
