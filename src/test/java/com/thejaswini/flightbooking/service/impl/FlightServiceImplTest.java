package com.thejaswini.flightbooking.service.impl;

import com.thejaswini.flightbooking.dto.FlightRequest;
import com.thejaswini.flightbooking.dto.FlightResponse;
import com.thejaswini.flightbooking.exception.DuplicateFlightException;
import com.thejaswini.flightbooking.mapper.FlightMapper;
import com.thejaswini.flightbooking.model.Flight;
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
 * Unit tests for {@link FlightServiceImpl} — verify flight-registration rules in isolation by
 * mocking the repository and mapper, so only the service logic is under test.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FlightService — flight registration")
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;
    @Mock
    private FlightMapper flightMapper;
    @InjectMocks
    private FlightServiceImpl service;

    /** Happy path: a not-yet-existing flight is saved and returned as a response DTO. */
    @Test
    @DisplayName("registers a new flight and returns it")
    void createsFlightWhenNew() {
        FlightRequest request = FlightRequest.builder()
                .flightNumber("AI-1").origin("BLR").destination("DXB").totalSeats(10).build();
        FlightResponse expected = FlightResponse.builder()
                .flightNumber("AI-1").totalSeats(10).availableSeats(10).build();
        when(flightRepository.saveIfAbsent(any(Flight.class))).thenReturn(Optional.empty());
        when(flightMapper.toResponse(any(Flight.class))).thenReturn(expected);

        FlightResponse result = service.createFlight(request);

        assertThat(result).isEqualTo(expected);
        verify(flightRepository).saveIfAbsent(any(Flight.class));
    }

    /** Idempotency/safety: registering an existing flight number is rejected and nothing is saved. */
    @Test
    @DisplayName("rejects a duplicate flight number and saves nothing")
    void rejectsDuplicateFlight() {
        FlightRequest request = FlightRequest.builder().flightNumber("AI-1").totalSeats(10).build();
        when(flightRepository.saveIfAbsent(any(Flight.class)))
                .thenReturn(Optional.of(new Flight("AI-1", "BLR", "DXB", 10)));

        assertThatThrownBy(() -> service.createFlight(request))
                .isInstanceOf(DuplicateFlightException.class);
        verify(flightMapper, never()).toResponse(any());
    }

    /** Defensive boundary check: a null request fails fast rather than NPE-ing deeper in the flow. */
    @Test
    @DisplayName("rejects a null request (defensive null-check)")
    void rejectsNullRequest() {
        assertThatThrownBy(() -> service.createFlight(null)).isInstanceOf(NullPointerException.class);
    }
}
