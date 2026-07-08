package com.thejaswini.flightbooking.constant;

/**
 * General application metadata constants (e.g. OpenAPI info) kept out of inline code.
 */
public final class AppConstants {

    /** Human-readable API title shown in Swagger UI / OpenAPI. */
    public static final String API_TITLE = "Flight Ticket Booking API";

    /** API version shown in OpenAPI. */
    public static final String API_VERSION = "1.0.0";

    /** Short API description shown in OpenAPI. */
    public static final String API_DESCRIPTION =
            "In-memory flight ticket booking service that never overbooks a flight.";

    /**
     * Prevents instantiation of this constants holder.
     */
    private AppConstants() {
    }
}
