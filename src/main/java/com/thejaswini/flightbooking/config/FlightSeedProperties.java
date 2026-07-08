package com.thejaswini.flightbooking.config;

import com.thejaswini.flightbooking.constant.ConfigKeys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds the environment-specific flight-seed configuration (see {@code application-*.yml}).
 *
 * <p>The {@code dev} profile enables sample flights; {@code prod} leaves seeding disabled. Keeping
 * the seed data in YAML (rather than in Java) makes it environment-specific without code changes.
 */
@ConfigurationProperties(prefix = ConfigKeys.SEED_PREFIX)
public class FlightSeedProperties {

    private boolean enabled;
    private List<SeedFlight> flights = new ArrayList<>();

    /** @return whether seeding is enabled for the active profile */
    public boolean isEnabled() {
        return enabled;
    }

    /** @param enabled whether seeding is enabled */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** @return the flights to seed (never {@code null}) */
    public List<SeedFlight> getFlights() {
        return flights;
    }

    /** @param flights the flights to seed */
    public void setFlights(List<SeedFlight> flights) {
        this.flights = (flights != null) ? flights : new ArrayList<>();
    }

    /**
     * A single seed flight entry, bound from YAML. Relaxed binding maps {@code flight-number} to
     * {@code flightNumber} and {@code total-seats} to {@code totalSeats}.
     */
    public static class SeedFlight {

        private String flightNumber;
        private String origin;
        private String destination;
        private int totalSeats;

        /** @return the flight number */
        public String getFlightNumber() {
            return flightNumber;
        }

        /** @param flightNumber the flight number */
        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        /** @return the departure location */
        public String getOrigin() {
            return origin;
        }

        /** @param origin the departure location */
        public void setOrigin(String origin) {
            this.origin = origin;
        }

        /** @return the arrival location */
        public String getDestination() {
            return destination;
        }

        /** @param destination the arrival location */
        public void setDestination(String destination) {
            this.destination = destination;
        }

        /** @return the total seating capacity */
        public int getTotalSeats() {
            return totalSeats;
        }

        /** @param totalSeats the total seating capacity */
        public void setTotalSeats(int totalSeats) {
            this.totalSeats = totalSeats;
        }
    }
}
