package com.thejaswini.flightbooking.exception;

import com.thejaswini.flightbooking.constant.ErrorMessages;

/**
 * Thrown when a booking requests more seats than are available, which is what prevents
 * overbooking. Maps to HTTP 409.
 */
public class InsufficientSeatsException extends ApiException {

    /**
     * @param flightNumber the flight being booked
     * @param available    seats currently available on the flight
     * @param requested    seats requested by the client
     */
    public InsufficientSeatsException(String flightNumber, int available, int requested) {
        super(ErrorCode.INSUFFICIENT_SEATS,
                String.format(ErrorMessages.INSUFFICIENT_SEATS, flightNumber, available, requested));
    }
}
