package com.thejaswini.flightbooking.repository.impl;

import com.thejaswini.flightbooking.constant.ValidationMessages;
import com.thejaswini.flightbooking.model.Flight;
import com.thejaswini.flightbooking.repository.FlightRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory {@link FlightRepository} backed by a {@link ConcurrentHashMap} keyed by flight number.
 */
@Repository
public class InMemoryFlightRepository implements FlightRepository {

    private final ConcurrentMap<String, Flight> flights = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Optional<Flight> findByFlightNumber(String flightNumber) {
        Objects.requireNonNull(flightNumber, ValidationMessages.FLIGHT_NUMBER_REQUIRED);
        return Optional.ofNullable(flights.get(flightNumber));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Flight> saveIfAbsent(Flight flight) {
        Objects.requireNonNull(flight, ValidationMessages.FLIGHT_REQUIRED);
        return Optional.ofNullable(flights.putIfAbsent(flight.getFlightNumber(), flight));
    }
}
