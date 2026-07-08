package com.thejaswini.flightbooking.service.impl;

import com.thejaswini.flightbooking.constant.ValidationMessages;
import com.thejaswini.flightbooking.dto.FlightRequest;
import com.thejaswini.flightbooking.dto.FlightResponse;
import com.thejaswini.flightbooking.exception.DuplicateFlightException;
import com.thejaswini.flightbooking.model.Flight;
import com.thejaswini.flightbooking.repository.FlightRepository;
import com.thejaswini.flightbooking.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Default {@link FlightService} backed by a {@link FlightRepository}.
 */
@Service
public class FlightServiceImpl implements FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightServiceImpl.class);

    private final FlightRepository flightRepository;

    /**
     * @param flightRepository storage for flights (injected)
     */
    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /** {@inheritDoc} */
    @Override
    public FlightResponse createFlight(FlightRequest request) {
        Objects.requireNonNull(request, ValidationMessages.REQUEST_REQUIRED);
        if (flightRepository.existsByFlightNumber(request.flightNumber())) {
            throw new DuplicateFlightException(request.flightNumber());
        }
        Flight saved = flightRepository.save(new Flight(
                request.flightNumber(), request.origin(), request.destination(), request.totalSeats()));
        log.info("Registered flight {} with {} seats", saved.getFlightNumber(), saved.getTotalSeats());
        return FlightResponse.from(saved);
    }
}
