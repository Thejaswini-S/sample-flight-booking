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
 * Request body used to register a flight. Every field is validated at the web layer (via
 * {@code @Valid}) so malformed input is rejected with 400 <em>before</em> reaching the service.
 *
 * @param flightNumber unique flight identifier (required; 2-10 letters/digits/hyphens)
 * @param origin       departure location (informational; max 64 chars)
 * @param destination  arrival location (informational; max 64 chars)
 * @param totalSeats   total seating capacity (1..1000)
 */
public record FlightRequest(

        @Schema(example = "AI-101", description = "Unique flight number")
        @NotBlank(message = ValidationMessages.REQ_FLIGHT_NUMBER)
        @Pattern(regexp = ValidationConstraints.FLIGHT_NUMBER_REGEX,
                message = ValidationMessages.REQ_FLIGHT_NUMBER_FORMAT)
        String flightNumber,

        @Schema(example = "BLR", description = "Departure location (informational)")
        @Size(max = ValidationConstraints.LOCATION_MAX, message = ValidationMessages.REQ_ORIGIN_SIZE)
        String origin,

        @Schema(example = "DXB", description = "Arrival location (informational)")
        @Size(max = ValidationConstraints.LOCATION_MAX, message = ValidationMessages.REQ_DESTINATION_SIZE)
        String destination,

        @Schema(example = "180", description = "Total seating capacity")
        @Min(value = 1, message = ValidationMessages.REQ_TOTAL_SEATS_MIN)
        @Max(value = ValidationConstraints.TOTAL_SEATS_MAX, message = ValidationMessages.REQ_TOTAL_SEATS_MAX)
        int totalSeats) {

    /**
     * @return a new fluent {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link FlightRequest} (handy for tests and programmatic construction).
     */
    public static final class Builder {

        private String flightNumber;
        private String origin;
        private String destination;
        private int totalSeats;

        /**
         * @param flightNumber the unique flight number
         * @return this builder
         */
        public Builder flightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
            return this;
        }

        /**
         * @param origin the departure location
         * @return this builder
         */
        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        /**
         * @param destination the arrival location
         * @return this builder
         */
        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        /**
         * @param totalSeats the total seating capacity
         * @return this builder
         */
        public Builder totalSeats(int totalSeats) {
            this.totalSeats = totalSeats;
            return this;
        }

        /**
         * @return the constructed {@link FlightRequest}
         */
        public FlightRequest build() {
            return new FlightRequest(flightNumber, origin, destination, totalSeats);
        }
    }
}
