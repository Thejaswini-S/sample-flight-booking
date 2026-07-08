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
     * Reports whether a flight with the given number already exists.
     *
     * @param flightNumber the flight number to check (must not be {@code null})
     * @return {@code true} if a flight with that number is stored
     */
    boolean existsByFlightNumber(String flightNumber);

    /**
     * Stores (inserts or replaces) the given flight, keyed by its flight number.
     *
     * @param flight the flight to persist (must not be {@code null})
     * @return the stored flight instance
     */
    Flight save(Flight flight);
}
