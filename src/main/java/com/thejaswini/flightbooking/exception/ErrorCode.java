package com.thejaswini.flightbooking.exception;

import org.springframework.http.HttpStatus;

/**
 * Catalog of application errors.
 *
 * <p>Each entry pairs a stable, machine-readable code (pattern {@code FB-<HTTP>-<NNN>}) with the
 * HTTP status returned to the client, so callers can branch on {@link #code()} without parsing
 * free-text messages.
 */
public enum ErrorCode {

    /** Request failed Bean Validation (missing/invalid fields). */
    VALIDATION_ERROR("FB-400-001", HttpStatus.BAD_REQUEST),

    /** Request body was malformed / unparseable. */
    MALFORMED_REQUEST("FB-400-002", HttpStatus.BAD_REQUEST),

    /** Referenced flight does not exist. */
    FLIGHT_NOT_FOUND("FB-404-001", HttpStatus.NOT_FOUND),

    /** Not enough seats available to fulfil the booking. */
    INSUFFICIENT_SEATS("FB-409-001", HttpStatus.CONFLICT),

    /** A flight with the same number already exists. */
    DUPLICATE_FLIGHT("FB-409-002", HttpStatus.CONFLICT),

    /** Unexpected server-side failure. */
    INTERNAL_ERROR("FB-500-001", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus status;

    /**
     * @param code   stable error code string
     * @param status HTTP status to return for this error
     */
    ErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    /** @return the stable error code string, e.g. {@code FB-409-001} */
    public String code() {
        return code;
    }

    /** @return the HTTP status associated with this error */
    public HttpStatus status() {
        return status;
    }
}
