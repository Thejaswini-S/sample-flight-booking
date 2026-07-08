package com.thejaswini.flightbooking.dto;

import java.time.Instant;

/**
 * Uniform error payload returned for every handled error, so clients always get the same shape.
 *
 * @param timestamp when the error was produced
 * @param status    HTTP status code
 * @param errorCode stable application error code (e.g. {@code FB-409-001})
 * @param error     error category name (the {@code ErrorCode} enum constant)
 * @param message   human-readable detail
 * @param path      request path that produced the error
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String error,
        String message,
        String path) {
}
