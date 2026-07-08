package com.thejaswini.flightbooking.controller;

import com.thejaswini.flightbooking.constant.ApiPaths;
import com.thejaswini.flightbooking.dto.FlightRequest;
import com.thejaswini.flightbooking.dto.FlightResponse;
import com.thejaswini.flightbooking.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for registering flights (an admin/seed helper so clients have flights to book).
 */
@RestController
@RequestMapping(ApiPaths.FLIGHTS)
@Tag(name = "Flights", description = "Register flights so they can be booked")
public class FlightController {

    private final FlightService flightService;

    /**
     * @param flightService flight application service (injected)
     */
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Registers a new flight.
     *
     * @param request the flight to create (validated)
     * @return the created flight, returned with HTTP 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new flight")
    public FlightResponse create(@Valid @RequestBody FlightRequest request) {
        return flightService.createFlight(request);
    }
}
