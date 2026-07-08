package com.thejaswini.flightbooking.constant;

/**
 * Centralized configuration keys/prefixes used for binding externalized (YAML/env) properties.
 *
 * <p>Referencing these constants instead of repeating raw property strings keeps the binding
 * contract between the code and the {@code application-*.yml} files in a single place.
 */
public final class ConfigKeys {

    /**
     * Prefix for flight-seed configuration. The set of seeded flights is environment-specific
     * (enabled with sample data under the {@code dev} profile, disabled under {@code prod}),
     * so it is supplied via the active profile's YAML rather than hard-coded in Java.
     */
    public static final String SEED_PREFIX = "flight-booking.seed";

    /**
     * Prevents instantiation of this constants holder.
     */
    private ConfigKeys() {
    }
}
