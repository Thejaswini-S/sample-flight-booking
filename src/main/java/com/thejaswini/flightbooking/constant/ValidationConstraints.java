package com.thejaswini.flightbooking.constant;

/**
 * Centralized constraint parameters (regex patterns, size/range bounds) referenced by the Bean
 * Validation annotations on request DTOs, so no limits are hard-coded inline.
 */
public final class ValidationConstraints {

    /** Allowed flight-number format: 2-10 letters, digits, or hyphens (e.g. {@code AI-101}). */
    public static final String FLIGHT_NUMBER_REGEX = "^[A-Za-z0-9-]{2,10}$";

    /** Maximum length for the informational origin/destination fields. */
    public static final int LOCATION_MAX = 64;

    /** Maximum length for a passenger name. */
    public static final int PASSENGER_NAME_MAX = 100;

    /** Maximum seats bookable in a single request. */
    public static final int SEATS_MAX = 50;

    /** Maximum total capacity accepted when registering a flight. */
    public static final int TOTAL_SEATS_MAX = 1000;

    /**
     * Prevents instantiation of this constants holder.
     */
    private ValidationConstraints() {
    }
}
