package com.thejaswini.flightbooking.dto;

import com.thejaswini.flightbooking.constant.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body used to register a flight.
 *
 * @param flightNumber unique flight identifier (required)
 * @param origin       departure location (informational only; never used for search)
 * @param destination  arrival location (informational only; never used for search)
 * @param totalSeats   total seating capacity (at least 1)
 */
public record FlightRequest(

        @Schema(example = "AI-101", description = "Unique flight number")
        @NotBlank(message = ValidationMessages.REQ_FLIGHT_NUMBER)
        String flightNumber,

        @Schema(example = "BLR", description = "Departure location (informational)")
        String origin,

        @Schema(example = "DXB", description = "Arrival location (informational)")
        String destination,

        @Schema(example = "180", description = "Total seating capacity")
        @Min(value = 1, message = ValidationMessages.REQ_TOTAL_SEATS_MIN)
        int totalSeats) {
}
