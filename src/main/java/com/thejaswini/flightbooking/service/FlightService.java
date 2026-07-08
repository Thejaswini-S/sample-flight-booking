package com.thejaswini.flightbooking.service;

import com.thejaswini.flightbooking.dto.FlightRequest;
import com.thejaswini.flightbooking.dto.FlightResponse;

/**
 * Application operations for managing flights.
 */
public interface FlightService {

    /**
     * Registers a new flight.
     *
     * @param request the flight to create (already validated at the web layer)
     * @return the created flight as a response DTO
     * @throws com.thejaswini.flightbooking.exception.DuplicateFlightException
     *         if a flight with the same number already exists
     */
    FlightResponse createFlight(FlightRequest request);
}
