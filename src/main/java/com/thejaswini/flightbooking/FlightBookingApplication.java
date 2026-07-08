package com.thejaswini.flightbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point for the Flight Ticket Booking API.
 *
 * <p>In-memory, single-instance service that lets clients book seats on a known flight
 * without ever overbooking it. See {@code plan.md} for the full design.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FlightBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightBookingApplication.class, args);
    }
}
