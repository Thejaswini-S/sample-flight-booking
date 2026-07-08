package com.thejaswini.flightbooking.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    void concurrentReservesNeverOverbook() throws InterruptedException, ExecutionException {
        int capacity = 50;
        int attempts = 200;
        Flight flight = new Flight("AI-1", "BLR", "DXB", capacity);
        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < attempts; i++) {
            futures.add(pool.submit(() -> {
                startGate.await();          // hold every thread here...
                return flight.reserve(1);   // ...then release them into reserve() together
            }));
        }
        startGate.countDown();              // start all attempts simultaneously

        int success = 0;
        for (Future<Boolean> future : futures) {
            if (Boolean.TRUE.equals(future.get())) {   // get() also surfaces any thrown exception
                success++;
            }
        }
        pool.shutdown();

        assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        assertThat(success).isEqualTo(capacity);
        assertThat(flight.getAvailableSeats()).isZero();
    }

    /** Compensating action: release() returns previously reserved seats back to availability. */
    @Test
    @DisplayName("release() restores previously reserved seats")
    void releaseRestoresSeats() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 10);
        flight.reserve(4);
        flight.release(3);
        assertThat(flight.getAvailableSeats()).isEqualTo(9);
    }

    /** Fail-fast: release() rejects returning more seats than are currently reserved (bug guard). */
    @Test
    @DisplayName("release() rejects releasing more than is reserved")
    void releaseRejectsOverRelease() {
        Flight flight = new Flight("AI-1", "BLR", "DXB", 10);
        flight.reserve(2);
        assertThatThrownBy(() -> flight.release(3)).isInstanceOf(IllegalStateException.class);
    }

    /** Flight numbers are canonicalized (trimmed + upper-cased) so lookups are case-insensitive. */
    @Test
    @DisplayName("normalizeNumber() trims and upper-cases; the constructor stores the canonical form")
    void normalizeNumberCanonicalizes() {
        assertThat(Flight.normalizeNumber("  ai-101 ")).isEqualTo("AI-101");
        assertThat(new Flight("ai-202", "BLR", "DXB", 5).getFlightNumber()).isEqualTo("AI-202");
    }
}
