package com.thejaswini.flightbooking.constant;

/**
 * Centralized human-readable messages for validation failures and defensive null/argument checks.
 *
 * <p>Keeping every message in one place avoids scattered string literals across the codebase,
 * makes wording consistent, and provides a single point to later externalize into i18n bundles.
 * All values are compile-time constants so they can be used inside Bean Validation annotations.
 */
public final class ValidationMessages {

    // --- Domain: Flight ------------------------------------------------------------------------
    /** Flight number reference must be provided. */
    public static final String FLIGHT_NUMBER_REQUIRED = "flightNumber must not be null";
    /** Total seat capacity must be greater than zero. */
    public static final String TOTAL_SEATS_POSITIVE = "totalSeats must be positive";
    /** Number of seats to reserve must be greater than zero. */
    public static final String RESERVE_SEATS_POSITIVE = "seats to reserve must be positive";
    /** Flight instance passed to persistence must be provided. */
    public static final String FLIGHT_REQUIRED = "flight must not be null";

    // --- Domain: Booking -----------------------------------------------------------------------
    /** Booking identifier must be provided. */
    public static final String BOOKING_ID_REQUIRED = "id must not be null";
    /** Passenger name must be provided. */
    public static final String PASSENGER_NAME_REQUIRED = "passengerName must not be null";
    /** Booking creation timestamp must be provided. */
    public static final String CREATED_AT_REQUIRED = "createdAt must not be null";
    /** Booked seat count must be greater than zero. */
    public static final String BOOKING_SEATS_POSITIVE = "seats must be positive";
    /** Booking instance passed to persistence must be provided. */
    public static final String BOOKING_REQUIRED = "booking must not be null";

    // --- API request DTO validation ------------------------------------------------------------
    /** Request field {@code flightNumber} is mandatory. */
    public static final String REQ_FLIGHT_NUMBER = "flightNumber is required";
    /** Request field {@code passengerName} is mandatory. */
    public static final String REQ_PASSENGER_NAME = "passengerName is required";
    /** Request field {@code seats} must be at least one. */
    public static final String REQ_SEATS_MIN = "seats must be at least 1";
    /** Request field {@code totalSeats} must be at least one. */
    public static final String REQ_TOTAL_SEATS_MIN = "totalSeats must be at least 1";

    /** Generic request body must be provided (service-level null guard). */
    public static final String REQUEST_REQUIRED = "request must not be null";

    /**
     * Prevents instantiation of this constants holder.
     */
    private ValidationMessages() {
    }
}
