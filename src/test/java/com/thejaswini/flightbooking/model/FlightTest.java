package com.thejaswini.flightbooking.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the {@link Flight} aggregate — the single place that enforces the "no overbooking"
 * rule. Verified in isolation (no Spring, no mocks) because this is the most safety-critical logic
 * in the service, including its behaviour under concurrency.
 */
@DisplayName("Flight aggregate — no-overbooking guard")
class FlightTest {

    /** Happy path: reserving within capacity succeeds and decrements availability by exactly that many. */
    @Test
    @DisplayName("reserve() decrements availability when enough seats are free")
    void reserveReducesAvailableSeats() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 10);
        assertThat(flight.reserve(3)).isTrue();
        assertThat(flight.getAvailableSeats()).isEqualTo(7);
    }

    /** The core rule: a request for more seats than remain is rejected and leaves state untouched. */
    @Test
    @DisplayName("reserve() rejects an over-capacity request and does not change seats")
    void reserveMoreThanAvailableFailsWithoutChange() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 2);
        assertThat(flight.reserve(3)).isFalse();
        assertThat(flight.getAvailableSeats()).isEqualTo(2);
    }

    /** Boundary: booking exactly the remaining seats is allowed and sells the flight out to zero. */
    @Test
    @DisplayName("reserve() allows booking exactly the remaining seats (sell-out)")
    void reserveExactlyAvailableSucceeds() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 2);
        assertThat(flight.reserve(2)).isTrue();
        assertThat(flight.getAvailableSeats()).isZero();
    }

    /** Defensive input check: a zero/negative seat count is a caller error and must fail fast. */
    @Test
    @DisplayName("reserve() rejects a non-positive seat count")
    void reserveNonPositiveThrows() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 2);
        assertThatThrownBy(() -> flight.reserve(0)).isInstanceOf(IllegalArgumentException.class);
    }

    /** A flight must have positive capacity; constructing one with zero/negative capacity is invalid. */
    @Test
    @DisplayName("constructor rejects non-positive total capacity")
    void constructorRejectsNonPositiveCapacity() {
        assertThatThrownBy(() -> new Flight("AI-1", "BLR", "DXB", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Concurrency proof of the no-overbooking guarantee: 200 parallel single-seat bookings on a
     * 50-seat flight must yield exactly 50 successes with zero seats left — never negative. This is
     * the scenario that a naive check-then-decrement would fail.
     */
    @Test
    @DisplayName("reserve() never overbooks under concurrency (exactly capacity succeed)")
    void concurrentReservesNeverOverbook() throws InterruptedException {
        int capacity = 50;
        int attempts = 200;
        Flight flight = new Flight("AI-1", "BLR", "DXB", capacity);
        ExecutorService pool = Executors.newFixedThreadPool(16);
        AtomicInteger success = new AtomicInteger();

        for (int i = 0; i < attempts; i++) {
            pool.submit(() -> {
                if (flight.reserve(1)) {
                    success.incrementAndGet();
                }
            });
        }
        pool.shutdown();

        assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        assertThat(success.get()).isEqualTo(capacity);
        assertThat(flight.getAvailableSeats()).isZero();
    }
}
