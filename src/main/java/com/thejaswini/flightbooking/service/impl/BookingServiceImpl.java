package com.thejaswini.flightbooking.service.impl;

import com.thejaswini.flightbooking.constant.ValidationMessages;
import com.thejaswini.flightbooking.dto.BookingRequest;
import com.thejaswini.flightbooking.dto.BookingResponse;
import com.thejaswini.flightbooking.exception.FlightNotFoundException;
import com.thejaswini.flightbooking.exception.InsufficientSeatsException;
import com.thejaswini.flightbooking.model.Booking;
import com.thejaswini.flightbooking.model.Flight;
import com.thejaswini.flightbooking.repository.BookingRepository;
import com.thejaswini.flightbooking.repository.FlightRepository;
import com.thejaswini.flightbooking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Default {@link BookingService}.
 *
 * <p>Relies on the flight's atomic {@link Flight#reserve(int)} guard to ensure a flight is never
 * overbooked, then persists the confirmed booking. No explicit locking is needed here because the
 * check-and-decrement happens inside the flight's synchronized method.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    /**
     * @param flightRepository  storage for flights (injected)
     * @param bookingRepository storage for bookings (injected)
     */
    public BookingServiceImpl(FlightRepository flightRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    /** {@inheritDoc} */
    @Override
    public BookingResponse book(BookingRequest request) {
        Objects.requireNonNull(request, ValidationMessages.REQUEST_REQUIRED);

        Flight flight = flightRepository.findByFlightNumber(request.flightNumber())
                .orElseThrow(() -> new FlightNotFoundException(request.flightNumber()));

        if (!flight.reserve(request.seats())) {
            log.warn("Rejected booking on {}: requested {} but only {} available",
                    flight.getFlightNumber(), request.seats(), flight.getAvailableSeats());
            throw new InsufficientSeatsException(
                    flight.getFlightNumber(), flight.getAvailableSeats(), request.seats());
        }

        Booking booking = bookingRepository.save(new Booking(
                UUID.randomUUID(), flight.getFlightNumber(), request.passengerName(),
                request.seats(), Instant.now()));
        log.info("Confirmed booking {} on {} for {} seat(s)",
                booking.id(), booking.flightNumber(), booking.seats());
        return BookingResponse.from(booking);
    }
}
