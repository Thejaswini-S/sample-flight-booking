package com.thejaswini.flightbooking.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests that exercise the full HTTP stack (controllers, {@code @Valid} validation,
 * services, in-memory repositories, MapStruct mappers, and the global exception handler) with
 * seeding disabled, so each test controls its own data. These prove the end-to-end contract that
 * the unit tests verify in isolation.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Booking API — full HTTP integration")
class BookingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * The headline scenario: register a 2-seat flight, book both seats (201), then a third seat is
     * refused with 409 + FB-409-001 — proving no overbooking across the whole stack.
     */
    @Test
    @DisplayName("register → book both seats (201) → third seat overbooks (409)")
    void bookingFlowThenOverbookingIsRejected() throws Exception {
        // Register a 2-seat flight.
        mockMvc.perform(post("/api/v1/flights").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"IT-1\",\"origin\":\"BLR\",\"destination\":\"DXB\",\"totalSeats\":2}"))
                .andExpect(status().isCreated());

        // Book both seats -> 201.
        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"IT-1\",\"passengerName\":\"Thejaswini\",\"seats\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.seats").value(2));

        // One more seat would overbook -> 409.
        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"IT-1\",\"passengerName\":\"Late\",\"seats\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("FB-409-001"));
    }

    /** Booking a flight that was never registered returns the 404 not-found contract. */
    @Test
    @DisplayName("unknown flight returns 404 (FB-404-001)")
    void unknownFlightReturns404() throws Exception {
        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"ZZ-9\",\"passengerName\":\"X\",\"seats\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("FB-404-001"));
    }

    /** A blank/zero request body is rejected by @Valid at the boundary with the 400 contract. */
    @Test
    @DisplayName("invalid request body returns 400 (FB-400-001)")
    void invalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"\",\"passengerName\":\"\",\"seats\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("FB-400-001"));
    }

    /** Registering the same flight number twice returns 409 (FB-409-002) on the second attempt. */
    @Test
    @DisplayName("duplicate flight registration returns 409 (FB-409-002)")
    void duplicateFlightReturns409() throws Exception {
        String body = "{\"flightNumber\":\"IT-DUP\",\"origin\":\"BLR\",\"destination\":\"DXB\",\"totalSeats\":5}";
        mockMvc.perform(post("/api/v1/flights").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/flights").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("FB-409-002"));
    }

    /** A syntactically broken JSON body is rejected with 400 (FB-400-002 malformed request). */
    @Test
    @DisplayName("malformed JSON returns 400 (FB-400-002)")
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"AI-1\","))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("FB-400-002"));
    }

    /** Creating a flight echoes its details back with availableSeats == totalSeats. */
    @Test
    @DisplayName("create-flight response body reflects full availability")
    void createFlightReturnsBody() throws Exception {
        mockMvc.perform(post("/api/v1/flights").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightNumber\":\"IT-BODY\",\"origin\":\"BLR\",\"destination\":\"DXB\",\"totalSeats\":42}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("IT-BODY"))
                .andExpect(jsonPath("$.totalSeats").value(42))
                .andExpect(jsonPath("$.availableSeats").value(42));
    }
}
