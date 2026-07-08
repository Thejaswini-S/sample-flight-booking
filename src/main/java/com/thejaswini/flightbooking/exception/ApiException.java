package com.thejaswini.flightbooking.exception;

/**
 * Base class for domain exceptions that carry an {@link ErrorCode}.
 *
 * <p>The global exception handler uses the code to produce a uniform error response with the
 * correct HTTP status, so throwing a subclass is all a service needs to do to signal a
 * well-defined API error.
 */
public abstract class ApiException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * @param errorCode the catalog entry (code + HTTP status) describing this failure
     * @param message   human-readable detail included in the response and logs
     */
    protected ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /** @return the error-catalog entry for this exception */
    public ErrorCode errorCode() {
        return errorCode;
    }
}
