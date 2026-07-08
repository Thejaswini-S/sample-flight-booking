package com.thejaswini.flightbooking.controller;

import com.thejaswini.flightbooking.constant.ApiPaths;
import com.thejaswini.flightbooking.dto.BookingRequest;
import com.thejaswini.flightbooking.dto.BookingResponse;
import com.thejaswini.flightbooking.service.BookingService;
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
 * REST endpoints for booking seats on a known flight.
 */
@RestController
@RequestMapping(ApiPaths.BOOKINGS)
@Tag(name = "Bookings", description = "Book seats on a known flight (never overbooked)")
public class BookingController {

    private final BookingService bookingService;

    /**
     * @param bookingService booking application service (injected)
     */
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Books seats on an existing flight.
     *
     * @param request the booking to create (validated)
     * @return the confirmed booking, returned with HTTP 201 Created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book seats on a flight")
    public BookingResponse book(@Valid @RequestBody BookingRequest request) {
        return bookingService.book(request);
    }
}
