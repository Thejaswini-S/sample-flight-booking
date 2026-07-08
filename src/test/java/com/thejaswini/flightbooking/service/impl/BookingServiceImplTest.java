package com.thejaswini.flightbooking.service.impl;

import com.thejaswini.flightbooking.dto.BookingRequest;
import com.thejaswini.flightbooking.dto.BookingResponse;
import com.thejaswini.flightbooking.exception.FlightNotFoundException;
import com.thejaswini.flightbooking.exception.InsufficientSeatsException;
import com.thejaswini.flightbooking.mapper.BookingMapper;
import com.thejaswini.flightbooking.model.Booking;
import com.thejaswini.flightbooking.model.Flight;
import com.thejaswini.flightbooking.repository.BookingRepository;
import com.thejaswini.flightbooking.repository.FlightRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BookingServiceImpl} — verify the booking rules (including the overbooking
 * guard's effect at the service layer) with the repositories and mapper mocked, so only the service
 * orchestration is under test.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService — booking without overbooking")
class BookingServiceImplTest {

    @Mock
    private FlightRepository flightRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl service;

    /** Happy path: seats are reserved on the flight and the confirmed booking is persisted. */
    @Test
    @DisplayName("books seats, reduces availability, and persists the booking")
    void booksSeatsAndPersists() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 10);
        BookingResponse expected = BookingResponse.builder().flightNumber("AI-1").seats(2).build();
        when(flightRepository.findByFlightNumber("AI-1")).thenReturn(Optional.of(flight));
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(expected);

        BookingResponse result = service.book(
                BookingRequest.builder().flightNumber("AI-1").passengerName("Thejaswini").seats(2).build());

        assertThat(result).isEqualTo(expected);
        assertThat(flight.getAvailableSeats()).isEqualTo(8);
        verify(bookingRepository).save(any(Booking.class));
    }

    /** Booking a flight that does not exist yields 404-mapped FlightNotFoundException; nothing is saved. */
    @Test
    @DisplayName("throws FlightNotFoundException for an unknown flight (nothing persisted)")
    void throwsWhenFlightMissing() {
        when(flightRepository.findByFlightNumber("NOPE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.book(
                BookingRequest.builder().flightNumber("NOPE").passengerName("X").seats(1).build()))
                .isInstanceOf(FlightNotFoundException.class);
        verify(bookingRepository, never()).save(any());
    }

    /** The overbooking rule at the service level: an over-capacity request is rejected, seats untouched,
     *  and no booking is written. */
    @Test
    @DisplayName("throws InsufficientSeatsException and consumes no seats when overbooked")
    void throwsWhenInsufficientSeats() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 1);
        when(flightRepository.findByFlightNumber("AI-1")).thenReturn(Optional.of(flight));

        assertThatThrownBy(() -> service.book(
                BookingRequest.builder().flightNumber("AI-1").passengerName("X").seats(2).build()))
                .isInstanceOf(InsufficientSeatsException.class);
        assertThat(flight.getAvailableSeats()).isEqualTo(1);
        verify(bookingRepository, never()).save(any());
    }

    /** Defensive boundary check: a null request fails fast with a clear NPE. */
    @Test
    @DisplayName("rejects a null request (defensive null-check)")
    void rejectsNullRequest() {
        assertThatThrownBy(() -> service.book(null)).isInstanceOf(NullPointerException.class);
    }
}
