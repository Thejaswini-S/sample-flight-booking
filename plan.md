# plan.md — Flight Ticket Booking API

> **Purpose of this file:** This is the single source of truth / "door to exit" describing exactly what
> will be built. It is written so a brand-new agent (or developer) can pick up the project from scratch
> if the current agent fails. Review and approve before implementation starts.

- **Project:** Flight Ticket Booking REST API (take-home interview task)
- **Owner / decision-maker:** Thejaswini (git: `Thejaswini-S`, `sthejaswini2001@gmail.com`)
- **Developed by (AI):** GitHub Copilot — model **Claude Opus 4.8**
- **Status:** ✅ APPROVED (2026-07-08) — implementation in progress
- **Last updated:** 2026-07-08

---

## 1. Objective

Design and implement a small, correct, senior-quality REST API for booking flight tickets, using
Spring Boot + Java, with in-memory storage and **no overbooking** allowed. Only booking operations are
required (no retrieval/search of bookings). The exercise is AI-driven; every AI prompt is recorded and
committed, followed by a final manual-improvement commit.

---

## 2. Confirmed constraints (from the task + your instructions)

| # | Constraint | Source |
|---|-----------|--------|
| 1 | Spring Boot + Java, single instance | task |
| 2 | No auth / authz / rate limiting | task |
| 3 | No flight search or destination logic; client already knows the flight number | task |
| 4 | **In-memory storage only** (no database) | task |
| 5 | **No overbooking** — hard business rule | task |
| 6 | Only booking APIs required; **no** API to retrieve bookings | task |
| 7 | Appropriate HTTP methods + status codes | task |
| 8 | Runnable project, README (run steps, example requests, future improvements) | task |
| 9 | Every AI prompt committed (prompt in commit message); final manual commit | task |
| 10 | **Build tool: Gradle** (faster, cacheable) | you |
| 11 | **Port: 8081** | you |
| 12 | Git identity: name `Thejaswini`, username `Thejaswini-S`, email `sthejaswini2001@gmail.com` | you |
| 13 | Senior-level: SOLID, null-checks, exceptions, custom exception handler, custom error+status codes | you |
| 14 | Centralized logging folder, rolling logs, **7-day TTL** | you |
| 15 | Java utility to zip source/docs/readme, ignoring build/log/git noise | you |
| 16 | OpenAPI spec + Swagger UI + importable Postman collection | you |
| 17 | Unit tests + a couple of integration tests + example requests | you |
| 18 | Newman runs the Postman collection successfully | you |
| 19 | Handover docs: `plan.md`, `memory.md`, `checkpoint.md`, prompt-audit doc | you |
| 20 | Mention which AI model was used | you |
| 21 | AWS log ingestion / indexing = **future scope** (not now) | you |
| 22 | Use advanced Java features where they add value; don't force; be efficient | you |
| 23 | Agent must ask before deciding roadmap items; decisions are yours | you |

---

## 3. Decisions — ✅ APPROVED (recommendations accepted)

> Approved by the user on 2026-07-08 ("the plan looks good.. u can get started"). D6 added per the same message.

| # | Question | Approved choice |
|---|----------|-----------------|
| D1 | Seats per booking | **Multiple seats per booking** — realistic and properly exercises overbooking |
| D2 | How flights enter memory | **Seed at startup + admin `POST /flights`** — best for demo + tests |
| D3 | Booking cancellation endpoint | **Booking only** (match spec); can add later |
| D4 | Paginated `GET /flights` demo | **Skip** — pagination documented as future scope; keeps scope tight |
| D5 | GitHub remote | **Local history only** — agent provides exact `git remote add` + `push` commands |
| D6 | Containerization | **Add a small Dockerfile** (multi-stage) + `.dockerignore`; run on 8081 |

---

## 4. Technology stack

- **Language:** Java (target LTS — 17 or 21, confirmed against installed JDK in Phase 1)
- **Framework:** Spring Boot 3.x (`spring-boot-starter-web`, `spring-boot-starter-validation`)
- **Build:** Gradle (wrapper committed: `gradlew`, `gradlew.bat`)
- **API docs:** `springdoc-openapi-starter-webmvc-ui` (Swagger UI + `/v3/api-docs`)
- **Logging:** Logback (Spring Boot default) via `logback-spring.xml`, rolling with 7-day retention
- **Testing:** JUnit 5, Spring Boot Test, MockMvc, JaCoCo (coverage)
- **API testing:** Postman collection + Newman (Node.js CLI)
- **Storage:** In-memory `ConcurrentHashMap` behind repository interfaces

---

## 5. Architecture (SOLID, layered)

```
Controller  ->  Service (interface + impl)  ->  Repository (interface + in-memory impl)  ->  Model
     |                    |                                |
   DTOs            business rules                  thread-safe storage
     |          (overbooking guard)
Global exception handler (@RestControllerAdvice) + ErrorCode enum
```

- **Single Responsibility:** controllers only handle HTTP; services hold business rules; repositories only store.
- **Open/Closed + Dependency Inversion:** services/repos coded to interfaces; in-memory impl is swappable for a DB later without touching callers.
- **Interface Segregation:** narrow, purpose-specific interfaces.
- **DTOs** decouple the API contract from the domain model (no entity leakage).

---

## 6. Proposed project structure

```
sample-flight-application/
├─ build.gradle
├─ settings.gradle
├─ gradlew / gradlew.bat / gradle/wrapper/...
├─ Dockerfile           # small multi-stage image (D6)
├─ .dockerignore
├─ README.md
├─ .gitignore
├─ plan.md              # this file
├─ memory.md            # agent local context (running notes/assumptions)
├─ checkpoint.md        # progress log + any blocking questions
├─ user-input-prompts.txt   # verbatim audit of every user prompt (interviewer-facing)
├─ docs/
│   └─ openapi.yaml      # exported OpenAPI spec
├─ postman/
│   ├─ FlightBooking.postman_collection.json
│   └─ FlightBooking.postman_environment.json
├─ logs/                 # rolling logs (gitignored), created at runtime
├─ src/main/java/com/thejaswini/flightbooking/
│   ├─ FlightBookingApplication.java
│   ├─ controller/      BookingController, FlightController
│   ├─ service/         BookingService, FlightService (+ impl/)
│   ├─ repository/      FlightRepository, BookingRepository (+ impl/ in-memory)
│   ├─ model/           Flight, Booking, BookingStatus
│   ├─ dto/             BookingRequest/Response, FlightRequest/Response, ErrorResponse
│   ├─ exception/       Custom exceptions, ErrorCode enum, GlobalExceptionHandler
│   ├─ config/          DataInitializer (seed), OpenApiConfig
│   └─ util/            ProjectArchiver (zip utility, standalone main)
└─ src/test/java/com/thejaswini/flightbooking/
    ├─ service/         BookingServiceImplTest (unit, incl. concurrency)
    └─ controller/      BookingControllerIT, FlightControllerIT (integration)
```

---

## 7. Domain model

**Flight**
- `flightNumber` : String (unique id)
- `origin`, `destination`, `departureTime` : optional descriptive metadata (NOT used for search)
- `totalSeats` : int (> 0)
- `availableSeats` : int (0..totalSeats)
- Thread-safe `synchronized boolean tryReserve(int seats)` — atomic check-and-decrement to prevent overbooking

**Booking**
- `bookingId` : UUID
- `flightNumber` : String
- `passengerName` : String
- `numberOfSeats` : int (≥ 1)
- `status` : enum `CONFIRMED` (+ `CANCELLED` if D3=b)
- `createdAt` : Instant

---

## 8. REST API design

Base path: `/api/v1`

| Method | Path | Purpose | Success | Errors |
|--------|------|---------|---------|--------|
| POST | `/bookings` | Create a booking (core) | `201 Created` + booking | `400` validation, `404` flight not found, `409` insufficient seats |
| POST | `/flights` | Register a flight (seed helper) [D2] | `201 Created` + flight | `400` validation, `409` duplicate flight |
| (opt) DELETE | `/bookings/{id}` | Cancel booking [D3=b] | `200 OK` | `404` not found |
| (opt) GET | `/flights` | Paginated listing [D4=b] | `200 OK` page | `400` bad params |

**Status-code policy:** 201 create, 400 validation/null, 404 missing flight/booking, 409 overbooking/duplicate, 500 unexpected.

---

## 9. Overbooking / concurrency strategy

- Single instance, but concurrent HTTP requests can race on the same flight.
- Atomic **check-and-decrement** inside a `synchronized` method on the `Flight` aggregate (or a per-flight lock in the repository). Guarantees total booked seats never exceed `totalSeats`.
- A dedicated concurrency unit test fires many parallel booking requests and asserts availableSeats never goes negative and exactly the right number succeed.

---

## 10. Exception handling & custom error codes

- Custom exceptions: `FlightNotFoundException`, `InsufficientSeatsException`, `DuplicateFlightException` (+ `BookingNotFoundException` if D3=b).
- `ErrorCode` enum: `{ code, httpStatus, defaultMessage }` e.g. `FB-404-001 FLIGHT_NOT_FOUND`, `FB-409-001 INSUFFICIENT_SEATS`, `FB-409-002 DUPLICATE_FLIGHT`, `FB-400-001 VALIDATION_ERROR`.
- `GlobalExceptionHandler` (`@RestControllerAdvice`) → uniform `ErrorResponse`:
  ```json
  { "timestamp":"...", "status":409, "errorCode":"FB-409-001",
    "error":"INSUFFICIENT_SEATS", "message":"...", "path":"/api/v1/bookings" }
  ```
- Bean Validation (`@NotBlank`, `@NotNull`, `@Min(1)`) enforces null-checks/input validation at the boundary.

---

## 11. Logging (centralized, rolling, 7-day TTL)

- `logback-spring.xml`: console + `RollingFileAppender`.
- Directory: `logs/` (configurable via property), file `flight-booking.log`.
- `TimeBasedRollingPolicy`, `fileNamePattern=flight-booking.%d{yyyy-MM-dd}.%i.log.gz`, `maxHistory=7` (days), size cap per file + `totalSizeCap`.
- Log key events: flight created, booking requested, booking confirmed, overbooking rejected, validation failure.
- **Future scope (documented, not built now):** ship logs to AWS (CloudWatch/S3) + indexing.

---

## 12. Testing strategy & Definition of Done

**Unit tests**
- `BookingServiceImplTest`: success, flight-not-found, insufficient-seats, boundary (book exactly remaining), concurrency (parallel bookings, no overbooking).
- `FlightServiceImplTest`: create, duplicate rejection, validation.

**Integration tests** (`@SpringBootTest` + MockMvc)
- Book happy-path → 201; overbook → 409; unknown flight → 404; invalid body → 400.

**Coverage:** JaCoCo; target high coverage of service/business logic (the "required lines").

**Definition of DONE (yours):**
1. ✅ All unit + integration tests pass.
2. ✅ Required booking/overbooking logic implemented.
3. ✅ Required lines covered by tests (JaCoCo report).
4. ✅ Postman collection endpoints created **and Newman run succeeds**.
5. ✅ App runs on **port 8081**; Swagger UI + OpenAPI available.
6. ✅ Rolling logs with 7-day retention produced.
7. ✅ Zip utility produces a clean archive.
8. ✅ Docs complete: `plan.md`, `memory.md`, `checkpoint.md`, `user-input-prompts.txt`, `README.md`.
9. ✅ Dockerfile builds and runs the app on port 8081.
9. ✅ Git history: one commit per prompt (prompt in message) + final manual-improvement commit.

---

## 13. OpenAPI / Swagger / Postman / Newman

- springdoc auto-generates OpenAPI; Swagger UI at `/swagger-ui.html`, JSON at `/v3/api-docs`; exported `docs/openapi.yaml`.
- Postman collection with example requests: create flight, successful booking, overbooking (→409), validation error (→400), unknown flight (→404). Environment file sets `baseUrl=http://localhost:8081`.
- Newman script runs the collection headlessly and must pass (requires Node.js/npm — verified in Phase 1; if absent I'll flag it as a blocker).

---

## 14. Zip utility (`ProjectArchiver.java`)

- Standalone `main` (runnable via Gradle task or `java`).
- Includes: `src/`, `build.gradle`, `settings.gradle`, `gradlew*`, `gradle/`, `README.md`, `docs/`, `postman/`, the handover `.md` files.
- Excludes: `build/`, `.gradle/`, `.git/`, `logs/`, `.idea/`, `*.class`, `dist/`.
- Output: `dist/flight-booking-<yyyyMMdd-HHmmss>.zip`.

---

## 15. Git & prompt-audit workflow

- `git init`; local config `user.name=Thejaswini`, `user.email=sthejaswini2001@gmail.com`.
- **Many small, meaningful, LOCAL commits** for progressive review (the user reviews every commit; do **not** push). Each commit message references the **driving prompt** (task requirement); `user-input-prompts.txt` stores the full verbatim audit (prompt #, timestamp, model, what changed).
- After AI iterations, a **single final manual-improvement commit** describing what was improved manually and why, plus known limitations not fixed in time.
- I cannot push to GitHub for you; I'll provide exact `git remote add` + `git push` commands.

---

## 16. Execution roadmap (phases)

| Phase | Work | Exit criteria |
|-------|------|---------------|
| 0 | **Plan approval** (this file) | You approve / edit |
| 1 | Verify JDK/Gradle/Node/Git; scaffold Gradle project; docs skeleton | project builds empty |
| 2 | Model, repositories, services, DTOs, exceptions, error codes | compiles |
| 3 | Controllers, validation, global exception handler | compiles |
| 4 | DataInitializer (seed), OpenAPI config, port 8081 | app starts, Swagger loads |
| 5 | `logback-spring.xml` rolling logs, 7-day TTL | logs written to `logs/` |
| 6 | Unit + integration tests, JaCoCo | all tests green, coverage report |
| 7 | Postman collection + Newman run | Newman passes |
| 8 | `ProjectArchiver` zip utility | produces clean zip |
| 8b | Dockerfile (small, multi-stage) + `.dockerignore` (D6) | image builds & runs on 8081 |
| 9 | README (incl. **application-wise + infra-wise** improvements) + finalize docs | docs complete |
| 10 | Progressive local commits (prompt-referenced) + final manual commit + push steps | history ready |

> `checkpoint.md` is updated at the end of every phase. If I ever get stuck on a roadmap decision,
> I stop, log the question in `checkpoint.md`, and ask you — I will not silently decide.

---

## 17. Explicitly out of scope (future)

- AWS log ingestion + indexing (CloudWatch/S3/OpenSearch).
- Real database / persistence, distributed concerns, clustering.
- Authentication, authorization, rate limiting.
- Flight search / destination logic.
- Retrieve-bookings APIs.

---

## 18. Key assumptions (correct me if wrong)

1. Booking captures `passengerName` + `numberOfSeats` (+ flightNumber). No payment/seat-map.
2. Flights are identified solely by `flightNumber`.
3. "Required lines covered" = high JaCoCo coverage of service/business logic, not literally 100% of every getter.
4. Java LTS (17/21) and Gradle wrapper are acceptable; exact JDK confirmed in Phase 1.

---

**➡️ Action for you:** Approve this plan (and answer D1–D5 in §3). On approval I start at Phase 1.
