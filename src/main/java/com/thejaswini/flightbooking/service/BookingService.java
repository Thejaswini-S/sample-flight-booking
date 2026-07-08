package com.thejaswini.flightbooking.service;

import com.thejaswini.flightbooking.dto.BookingRequest;
import com.thejaswini.flightbooking.dto.BookingResponse;

/**
 * Application operations for booking seats on a flight.
 */
public interface BookingService {

    /**
     * Books seats on an existing flight without ever overbooking it.
     *
     * @param request the booking to create (already validated at the web layer)
     * @return the confirmed booking as a response DTO
     * @throws com.thejaswini.flightbooking.exception.FlightNotFoundException
     *         if the referenced flight does not exist
     * @throws com.thejaswini.flightbooking.exception.InsufficientSeatsException
     *         if not enough seats remain
     */
    BookingResponse book(BookingRequest request);
}
