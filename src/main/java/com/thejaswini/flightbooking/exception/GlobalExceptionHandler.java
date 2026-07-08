package com.thejaswini.flightbooking.exception;

import com.thejaswini.flightbooking.constant.ErrorMessages;
import com.thejaswini.flightbooking.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Translates exceptions into uniform {@link ErrorResponse} payloads with the correct HTTP status
 * and a stable {@link ErrorCode}.
 *
 * <p>Centralizing this keeps controllers free of try/catch noise and guarantees a consistent
 * error contract across the whole API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles all domain exceptions that carry an {@link ErrorCode}.
     *
     * @param ex  the thrown domain exception
     * @param req current request (used for the path)
     * @return the mapped error response with the code's HTTP status
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest req) {
        ErrorCode code = ex.errorCode();
        log.warn("Handled {} ({}) at {}: {}", code.name(), code.code(), req.getRequestURI(), ex.getMessage());
        return build(code, ex.getMessage(), req);
    }

    /**
     * Handles Bean Validation failures on {@code @Valid} request bodies.
     *
     * @param ex  the validation exception
     * @param req current request (used for the path)
     * @return a 400 response listing the invalid fields
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed at {}: {}", req.getRequestURI(), details);
        return build(ErrorCode.VALIDATION_ERROR, details, req);
    }

    /**
     * Handles unparseable / malformed JSON request bodies.
     *
     * @param ex  the parse exception
     * @param req current request (used for the path)
     * @return a 400 response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest req) {
        log.warn("Malformed request body at {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(ErrorCode.MALFORMED_REQUEST, ErrorMessages.MALFORMED_REQUEST, req);
    }

    /**
     * Handles requests to unmapped paths / missing static resources (e.g. {@code /favicon.ico}).
     * These are ordinary 404s, not server errors, so they are logged at debug level only.
     *
     * @param ex  the missing-resource exception
     * @param req current request (used for the path)
     * @return a 404 response
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        log.debug("No resource for {}", req.getRequestURI());
        return build(ErrorCode.RESOURCE_NOT_FOUND, ErrorMessages.RESOURCE_NOT_FOUND, req);
    }

    /**
     * Fallback for any otherwise-unhandled exception.
     *
     * @param ex  the unexpected exception
     * @param req current request (used for the path)
     * @return a 500 response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error at {}", req.getRequestURI(), ex);
        return build(ErrorCode.INTERNAL_ERROR, ErrorMessages.INTERNAL_ERROR, req);
    }

    /**
     * Builds a {@link ResponseEntity} carrying the uniform error body for the given code.
     *
     * @param code    the error catalog entry (HTTP status + code)
     * @param message human-readable detail
     * @param req     current request (used for the path)
     * @return the response entity with the correct status and body
     */
    private ResponseEntity<ErrorResponse> build(ErrorCode code, String message, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(), code.status().value(), code.code(), code.name(), message, req.getRequestURI());
        return ResponseEntity.status(code.status()).body(body);
    }
}
