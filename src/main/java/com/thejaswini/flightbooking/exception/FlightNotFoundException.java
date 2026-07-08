package com.thejaswini.flightbooking.exception;

import com.thejaswini.flightbooking.constant.ErrorMessages;

/**
 * Thrown when a booking references a flight number that does not exist. Maps to HTTP 404.
 */
public class FlightNotFoundException extends ApiException {

    /**
     * @param flightNumber the flight number that could not be found
     */
    public FlightNotFoundException(String flightNumber) {
        super(ErrorCode.FLIGHT_NOT_FOUND, String.format(ErrorMessages.FLIGHT_NOT_FOUND, flightNumber));
    }
}
