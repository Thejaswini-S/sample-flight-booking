package com.thejaswini.flightbooking.dto;

/**
 * API representation of a flight, including live seat availability.
 *
 * <p>Immutable value object constructed via its {@link Builder} (Builder pattern) — which the
 * generated MapStruct mapper also uses. Kept as a record so equality/toString come for free.
 *
 * @param flightNumber   unique flight identifier
 * @param origin         departure location (informational)
 * @param destination    arrival location (informational)
 * @param totalSeats     total seating capacity
 * @param availableSeats seats still available to book
 */
public record FlightResponse(
        String flightNumber,
        String origin,
        String destination,
        int totalSeats,
        int availableSeats) {

    /**
     * @return a new fluent {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link FlightResponse} (also used by the generated MapStruct mapper).
     */
    public static final class Builder {

        private String flightNumber;
        private String origin;
        private String destination;
        private int totalSeats;
        private int availableSeats;

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
         * @param availableSeats the seats still available to book
         * @return this builder
         */
        public Builder availableSeats(int availableSeats) {
            this.availableSeats = availableSeats;
            return this;
        }

        /**
         * @return the constructed immutable {@link FlightResponse}
         */
        public FlightResponse build() {
            return new FlightResponse(flightNumber, origin, destination, totalSeats, availableSeats);
        }
    }
}
