package com.thejaswini.flightbooking.constant;

/**
 * Centralized message templates for error responses raised by the service/web layers.
 *
 * <p>Templates use {@link String#format(String, Object...)} placeholders so callers supply
 * the dynamic parts (flight number, seat counts) without embedding literals inline.
 */
public final class ErrorMessages {

    /** Flight lookup failed. Format args: flightNumber. */
    public static final String FLIGHT_NOT_FOUND = "Flight not found: %s";

    /** Attempt to create a flight that already exists. Format args: flightNumber. */
    public static final String DUPLICATE_FLIGHT = "Flight already exists: %s";

    /** Not enough seats to satisfy a booking. Format args: flightNumber, available, requested. */
    public static final String INSUFFICIENT_SEATS =
            "Flight %s has only %d seat(s) available but %d were requested";

    /** Request body could not be parsed. */
    public static final String MALFORMED_REQUEST = "Malformed or unreadable request body";

    /** Requested path / static resource was not found. */
    public static final String RESOURCE_NOT_FOUND = "Requested resource was not found";

    /** Catch-all for unexpected server errors. */
    public static final String INTERNAL_ERROR = "An unexpected error occurred";

    /**
     * Prevents instantiation of this constants holder.
     */
    private ErrorMessages() {
    }
}
