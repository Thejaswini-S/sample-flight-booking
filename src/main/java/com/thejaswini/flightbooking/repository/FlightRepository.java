package com.thejaswini.flightbooking.repository;

import com.thejaswini.flightbooking.model.Flight;

import java.util.Optional;

/**
 * Storage abstraction for {@link Flight} aggregates. Coding to this interface keeps the
 * service layer independent of the storage technology (in-memory today, a database later).
 */
public interface FlightRepository {

    /**
     * Finds a flight by its unique number.
     *
     * @param flightNumber the flight number to look up (must not be {@code null})
     * @return the matching flight, or {@link Optional#empty()} if none exists
     */
    Optional<Flight> findByFlightNumber(String flightNumber);

    /**
     * Atomically stores the flight only if no flight with the same number already exists.
     *
     * @param flight the flight to insert (must not be {@code null})
     * @return {@link Optional#empty()} if the flight was inserted, or the existing flight if one
     *         with the same number was already present (nothing is stored in that case)
     */
    Optional<Flight> saveIfAbsent(Flight flight);
}
