package com.thejaswini.flightbooking.model;

import com.thejaswini.flightbooking.constant.ValidationMessages;

import java.util.Locale;
import java.util.Objects;

/**
 * A flight with a fixed seating capacity.
 *
 * <p>Seat reservation is atomic and thread-safe: {@link #reserve(int)} performs a single
 * check-and-decrement under the intrinsic lock, so the flight can never be overbooked even
 * when many booking requests arrive concurrently on this single application instance.
 */
public class Flight {

    private final String flightNumber;
    private final String origin;        // descriptive only — never used for search
    private final String destination;   // descriptive only — never used for search
    private final int totalSeats;
    private int availableSeats;

    /**
     * Creates a flight at full availability (available seats equal to total seats).
     *
     * @param flightNumber unique identifier clients use to book (required)
     * @param origin       descriptive departure location (optional; never used for search)
     * @param destination  descriptive arrival location (optional; never used for search)
     * @param totalSeats   fixed seating capacity (must be positive)
     * @throws NullPointerException     if {@code flightNumber} is {@code null}
     * @throws IllegalArgumentException if {@code totalSeats} is not positive
     */
    public Flight(String flightNumber, String origin, String destination, int totalSeats) {
        this.flightNumber = normalizeNumber(flightNumber);
        if (totalSeats <= 0) {
            throw new IllegalArgumentException(ValidationMessages.TOTAL_SEATS_POSITIVE);
        }
        this.origin = origin;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    /**
     * Canonicalizes a flight number so the same flight is never addressable under two spellings:
     * trims surrounding whitespace and upper-cases it. Flight numbers are therefore case-insensitive.
     *
     * @param flightNumber the raw flight number (must not be {@code null})
     * @return the canonical (trimmed, upper-cased) flight number
     * @throws NullPointerException if {@code flightNumber} is {@code null}
     */
    public static String normalizeNumber(String flightNumber) {
        Objects.requireNonNull(flightNumber, ValidationMessages.FLIGHT_NUMBER_REQUIRED);
        return flightNumber.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Atomically reserves {@code seats} when enough are available.
     *
     * @param seats number of seats to reserve (must be positive)
     * @return {@code true} if the reservation succeeded; {@code false} if there were not
     *         enough seats (in which case no state change occurs)
     */
    public synchronized boolean reserve(int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException(ValidationMessages.RESERVE_SEATS_POSITIVE);
        }
        if (seats > availableSeats) {
            return false;
        }
        availableSeats -= seats;
        return true;
    }

    /**
     * Returns previously reserved seats to availability (a compensating action used if a booking
     * fails to persist after a successful {@link #reserve(int)}).
     *
     * @param seats number of seats to release (must be positive and must not exceed the number
     *              currently reserved)
     * @throws IllegalArgumentException if {@code seats} is not positive
     * @throws IllegalStateException    if asked to release more seats than are currently reserved,
     *                                  which would indicate a double-release bug
     */
    public synchronized void release(int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException(ValidationMessages.RESERVE_SEATS_POSITIVE);
        }
        if (seats > totalSeats - availableSeats) {
            throw new IllegalStateException(ValidationMessages.RELEASE_EXCEEDS_RESERVED);
        }
        availableSeats += seats;
    }

    /** @return the unique flight number used to identify and book this flight */
    public String getFlightNumber() {
        return flightNumber;
    }

    /** @return the descriptive departure location, or {@code null} if not set */
    public String getOrigin() {
        return origin;
    }

    /** @return the descriptive arrival location, or {@code null} if not set */
    public String getDestination() {
        return destination;
    }

    /** @return the fixed total seating capacity of this flight */
    public int getTotalSeats() {
        return totalSeats;
    }

    /** @return the seats still available to book (read under the intrinsic lock for visibility) */
    public synchronized int getAvailableSeats() {
        return availableSeats;
    }
}
