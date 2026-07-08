package com.thejaswini.flightbooking.exception;

import com.thejaswini.flightbooking.constant.ErrorMessages;

/**
 * Thrown when creating a flight whose number is already registered. Maps to HTTP 409.
 */
public class DuplicateFlightException extends ApiException {

    /**
     * @param flightNumber the flight number that already exists
     */
    public DuplicateFlightException(String flightNumber) {
        super(ErrorCode.DUPLICATE_FLIGHT, String.format(ErrorMessages.DUPLICATE_FLIGHT, flightNumber));
    }
}
