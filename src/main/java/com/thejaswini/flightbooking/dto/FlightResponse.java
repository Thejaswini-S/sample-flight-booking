package com.thejaswini.flightbooking.dto;

import com.thejaswini.flightbooking.model.Flight;

/**
 * API representation of a flight, including live seat availability.
 *
 * @param flightNumber   unique flight identifier
 * @param origin         departure location (informational)
 * @param destination    arrival location (informational)
 * @param totalSeats     total seating capacity
 * @param availableSeats seats still available to book
 */
public record FlightResponse(
        String flightNumber,
        String origin,
        String destination,
        int totalSeats,
        int availableSeats) {

    /**
     * Maps a {@link Flight} domain object to its API representation.
     *
     * @param flight the domain flight (must not be {@code null})
     * @return the response DTO
     */
    public static FlightResponse from(Flight flight) {
        return new FlightResponse(flight.getFlightNumber(), flight.getOrigin(),
                flight.getDestination(), flight.getTotalSeats(), flight.getAvailableSeats());
    }
}
