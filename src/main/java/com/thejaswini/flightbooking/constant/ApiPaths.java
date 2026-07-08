package com.thejaswini.flightbooking.constant;

/**
 * Centralized REST API path constants.
 *
 * <p>All controllers reference these constants instead of hard-coding route strings inline,
 * so the URL contract lives in exactly one place. Values are compile-time constants and are
 * therefore usable directly inside annotations (e.g. {@code @RequestMapping}).
 */
public final class ApiPaths {

    /** API version prefix shared by every endpoint. */
    public static final String API_BASE = "/api/v1";

    /** Collection path for flight resources. */
    public static final String FLIGHTS = API_BASE + "/flights";

    /** Collection path for booking resources. */
    public static final String BOOKINGS = API_BASE + "/bookings";

    /**
     * Prevents instantiation of this constants holder.
     */
    private ApiPaths() {
    }
}
