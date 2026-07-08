# Flight Ticket Booking API

A small, in-memory REST API to **book seats on a known flight without ever overbooking it**.
Spring Boot + Java 17 (Gradle), single instance, no database, no auth — per the take-home brief.

> Developed AI-first with **GitHub Copilot (model: Claude Opus 4.8)**. Every AI prompt is recorded
> verbatim in [`user-input-prompts.txt`](user-input-prompts.txt), and each iteration is a commit whose
> message references its driving prompt. The epic was built across feature branches merged via PRs.

## Tech stack
- Java 17, Spring Boot 3.3, Gradle (wrapper included)
- MapStruct (compile-time mapping), Jakarta Bean Validation, springdoc OpenAPI / Swagger UI
- Zalando Logbook (HTTP logging), Spring Boot Actuator (health / info)
- JUnit 5 + Mockito + MockMvc + JaCoCo

## How to run
```bash
# from the project root  (Windows: .\gradlew.bat bootRun)
./gradlew bootRun
```
Starts on **http://localhost:8081**. `bootRun` activates the `dev` profile, which seeds sample
flights (`AI-101` = 3 seats, `AI-202` = 60, `AI-303` = 10) so you can book immediately. (The
packaged jar / Docker image runs profile-less by default — see Profiles below.)

- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs
- Health: http://localhost:8081/actuator/health

### Run with Docker
```bash
docker build -t flight-booking .
docker run -p 8081:8081 flight-booking
```

### Profiles
- `dev` — the **`bootRun` default** (local runs only): seeds sample flights, verbose logging.
- `prod` — no seed data, quieter logging: `./gradlew bootRun --args='--spring.profiles.active=prod'`.
- **No profile** — the packaged `jar` / Docker default: no seed data, INFO logging. Set
  `SPRING_PROFILES_ACTIVE` (or `--spring.profiles.active`) to choose a profile.

## API
Base path `/api/v1`.

| Method | Path | Purpose | Success | Errors |
|--------|------|---------|---------|--------|
| POST | `/flights` | Register a flight | `201` | `400`, `409` duplicate |
| POST | `/bookings` | Book seats (no overbooking) | `201` | `400`, `404`, `409` insufficient seats |

### Example requests
Register a flight:
```bash
curl -X POST http://localhost:8081/api/v1/flights -H "Content-Type: application/json" \
  -d '{"flightNumber":"BA-201","origin":"LHR","destination":"JFK","totalSeats":180}'
```
Book seats:
```bash
curl -X POST http://localhost:8081/api/v1/bookings -H "Content-Type: application/json" \
  -d '{"flightNumber":"AI-101","passengerName":"Thejaswini","seats":2}'
```
Overbooking is rejected with `409` and a uniform error body:
```json
{ "timestamp":"...", "status":409, "errorCode":"FB-409-001", "error":"INSUFFICIENT_SEATS",
  "message":"Flight AI-101 has only 1 seat(s) available but 2 were requested", "path":"/api/v1/bookings" }
```
Stable error codes: `FB-400-001` validation · `FB-404-001` flight not found · `FB-409-001`
insufficient seats · `FB-409-002` duplicate flight.

## Postman / Newman
Import [`postman/FlightBooking.postman_collection.json`](postman/FlightBooking.postman_collection.json)
with the [environment](postman/FlightBooking.postman_environment.json), or run it headless (start the app first):
```bash
npx newman run postman/FlightBooking.postman_collection.json -e postman/FlightBooking.postman_environment.json
```

## Tests
```bash
./gradlew test   # JaCoCo HTML report: <gradle-build-dir>/reports/jacoco/test/html/index.html
```
> Note: on OneDrive-synced checkouts the Gradle build dir is redirected out of the sync tree
> (see `build.gradle`), so `<gradle-build-dir>` may be a temp path rather than `./build`.
- **Unit** — `Flight` overbooking guard incl. a concurrency proof (200 parallel bookings on 50 seats → exactly 50 succeed); both services with mocked dependencies.
- **Integration** — full-stack MockMvc (`201` / `409` / `404` / `400`).

## Logs
Centralized rolling logs at `logs/flight-booking.log` — daily rotation, gzip history, **7-day retention**.

## Package the source (zip utility)
```bash
./gradlew -q runArchiver
```
Zips source + docs + README into `dist/flight-booking-<timestamp>.zip`, skipping `build/`, `logs/`,
`.git/`, `.gradle/`, `dist/`.

## What I'd improve with more time
**Application-wise**
- Booking **cancellation** and a paginated read API (with ETag / `If-Match` concurrency control).
- **Idempotency** on `POST /bookings` (e.g. an `Idempotency-Key` header) so a client retry after a
  timeout doesn't double-book.
- Have `Flight.reserve()` return the seat **shortfall** atomically instead of re-reading
  `getAvailableSeats()` after a failed reserve, so the insufficient-seats message stays consistent under load.
- Richer domain (fares, seat classes/maps, passenger records) + persistence (JPA + Flyway).
- **RFC 7807 Problem Details** (Zalando Problem) for errors; a formal API-versioning strategy.
- Property-based and load tests around the concurrency guard.

**Infra-wise**
- **Ship logs to AWS** (CloudWatch/S3) + indexing/search (OpenSearch) — designed-for, not built.
- **CI** (GitHub Actions): build, test, JaCoCo gate, Zally OpenAPI lint, container scan, image publish.
- Containerized deploy (ECS/Fargate or Kubernetes) with liveness/readiness probes wired to Actuator.
- **Observability**: metrics (Micrometer → Prometheus/Grafana), tracing (OpenTelemetry).
- Horizontal scaling would require moving seat state out of process memory (e.g., Redis or a DB row
  with an atomic decrement / optimistic locking) since the current guard is per-instance by design.
