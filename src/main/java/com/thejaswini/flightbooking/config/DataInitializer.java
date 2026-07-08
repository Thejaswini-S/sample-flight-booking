package com.thejaswini.flightbooking.config;

import com.thejaswini.flightbooking.dto.FlightRequest;
import com.thejaswini.flightbooking.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the in-memory store with the flights configured for the active profile (only the {@code dev}
 * profile enables seeding by default). Runs once at startup and does nothing when seeding is off.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final FlightSeedProperties seedProperties;
    private final FlightService flightService;

    /**
     * @param seedProperties bound seed configuration
     * @param flightService  service used to register each seed flight (reuses duplicate checks)
     */
    public DataInitializer(FlightSeedProperties seedProperties, FlightService flightService) {
        this.seedProperties = seedProperties;
        this.flightService = flightService;
    }

    /**
     * Registers each configured seed flight at application startup.
     *
     * @param args the incoming application arguments (unused)
     */
    @Override
    public void run(ApplicationArguments args) {
        if (!seedProperties.isEnabled()) {
            log.info("Flight seeding is disabled for the active profile");
            return;
        }
        seedProperties.getFlights().forEach(seed -> {
            flightService.createFlight(new FlightRequest(
                    seed.getFlightNumber(), seed.getOrigin(), seed.getDestination(), seed.getTotalSeats()));
            log.info("Seeded flight {} ({} seats)", seed.getFlightNumber(), seed.getTotalSeats());
        });
    }
}
