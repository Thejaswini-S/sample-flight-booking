# memory.md — Agent Local Context

> **Role of this file:** the agent's working memory / local context. Running notes, decisions,
> conventions, and gotchas needed to continue the build efficiently or hand it over to a new agent.

- **Last updated:** 2026-07-08
- **AI model driving development:** GitHub Copilot — **Claude Opus 4.8**

---

## Project in one line
In-memory Spring Boot (Gradle) REST API to **book flight tickets without overbooking**, on **port 8081**,
with senior-grade structure (SOLID, custom exceptions/error codes), rolling logs (7-day TTL),
OpenAPI/Swagger, Postman+Newman, unit+integration tests, a Java zip utility, and a Dockerfile.

## Confirmed decisions (locked)
- Build: **Gradle** (wrapper committed). Java LTS (verify installed JDK in Phase 1).
- Spring Boot 3.x, `spring-boot-starter-web` + `-validation`.
- Port **8081** (`server.port=8081`).
- Git identity (local repo): name `Thejaswini`, email `sthejaswini2001@gmail.com`, GitHub `Thejaswini-S`.
- Base path `/api/v1`. Package root `com.thejaswini.flightbooking`.
- **D1** multiple seats per booking · **D2** seed flights at startup + `POST /flights` ·
  **D3** booking only (no cancel) · **D4** no pagination/listing (documented as future) ·
  **D5** local git history only, provide push commands · **D6** small Dockerfile (added).

## Environment (verified 2026-07-08 10:46)
- Java **17.0.19 LTS** → toolchain target **17**. Git 2.55. Node v24.15 / npm 11.6. Docker available.
- Gradle available (scoop) → wrapper generated via `gradle wrapper`.
- **Newman not installed** → run via `npx newman` (no global install).
- Spring Boot **3.3.x** + springdoc-openapi **2.6.x** + JaCoCo (Gradle plugin).

## Conventions
- Layered: controller → service(iface+impl) → repository(iface+in-memory impl) → model. DTOs at boundary.
- No entity leakage; DTO mapping in service/controller.
- Custom error codes `FB-<HTTP>-<NNN>` returned in a uniform `ErrorResponse`.
- Overbooking prevented by an atomic `synchronized` check-and-decrement on the `Flight` aggregate.
- **No inline/class-level literals** — use the `constant/` package (`ApiPaths`, `ValidationMessages`, `ConfigKeys`) or profile YAML.
- **Full Javadoc on every public AND private method.** Quality over quantity — every line meaningful, no filler.
- **Config via profiles:** `application.yml` (common; default profile `dev`) + `application-dev.yml` (seeds sample flights, DEBUG) + `application-prod.yml` (no seed, quieter). Seed bound via `@ConfigurationProperties(prefix=flight-booking.seed)`.
- **Keep audits current:** timestamp every prompt in `user-input-prompts.txt` and every commit in `time-tracker.md`.
- Commits: **many small, meaningful commits**, each referencing its driving prompt (see
  `user-input-prompts.txt`). Delivered via `feature/*` branches → PRs into `master` (user approves each).
  Final commit = manual-improvement summary.

## Git / remote / branching
- Remote `origin` = https://github.com/Thejaswini-S/sample-flight-booking (started empty).
- Integration branch: **`master`** (renamed from `main`).
- Auth: gh active account = **Thejaswini-S** (push + PRs). Commit author = `Thejaswini <sthejaswini2001@gmail.com>`.
- Strategy: foundation (docs/scaffold/config/domain) = `master` baseline; each epic on a `feature/*`
  branch → PR → user manually approves → merge. **One PR at a time** (gated on review).
- Planned feature branches: `feature/booking-api`, `feature/observability`, `feature/testing`,
  `feature/api-collection`, `feature/tooling`, `feature/docs`.

## Key files (planned)
- Prompt audit: `user-input-prompts.txt` (verbatim, all prompts).
- Handover: `plan.md`, `memory.md` (this), `checkpoint.md`.
- App entry: `src/main/java/com/thejaswini/flightbooking/FlightBookingApplication.java`.
- Logging: `src/main/resources/logback-spring.xml` → `logs/` (rolling, `maxHistory=7`).

## Gotchas / reminders
- No database, no auth, no search, no retrieve-bookings API (per task spec).
- Do NOT commit `build/`, `.gradle/`, `logs/`, `dist/` (add to `.gitignore`).
- Newman needs Node.js/npm — verify in Phase 1; if missing, flag to user (blocker for that step only).
- README must include example requests + **application-wise and infra-wise** improvements.
- If a roadmap decision is unclear → stop, log question in `checkpoint.md`, ask the user (do not self-decide).

## Open questions to user
- (none currently — D1–D6 resolved)
