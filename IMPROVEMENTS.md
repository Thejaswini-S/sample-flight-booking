# Manual Improvements & Known Limitations

> Task Step 2 — a critical review of the AI-generated solution: what was improved (and why), and the
> major issues that remain within the ~60-minute scope. The final commit references this file.

## Improvements made over the initial AI scaffold
1. **Boundary validation hardening** — added `@Pattern` / `@Size` / `@Max` on the request DTOs (limits
   centralized in `ValidationConstraints`) so malformed/oversized input is rejected at the controller
   (400) instead of flowing into the service. *Why:* keep bad requests out of the domain; clearer contract.
2. **Correct 404 vs 500** — a `NoResourceFoundException` handler returns 404 for unknown paths /
   `/favicon.ico` instead of the catch-all turning them into 500s with noisy ERROR logs. *Why:* accurate
   status codes and quieter logs.
3. **MapStruct over ModelMapper** — compile-time, type-safe mapping, no runtime reflection. *Why:* mapping
   errors caught at build time; better performance.
4. **Builder pattern on DTOs** — fluent, readable construction (also used by the generated mappers/tests).
5. **Thread-safe overbooking guard** — atomic `synchronized` check-and-decrement on the `Flight`
   aggregate, proven by a concurrency test (200 parallel bookings on 50 seats → exactly 50 succeed).
6. **Centralized constants + full Javadoc** — no inline/class-level literals; every method documented.
7. **Environment profiles** — `dev` seeds sample data, `prod` starts empty (config-driven, not hard-coded).
8. **Test intent made explicit** — every test carries a `@DisplayName` and a purpose comment.

## Major known issues / not addressed in the time box
- **In-memory, single-instance state.** The overbooking guard is per-JVM; horizontal scaling would need
  seat state in a shared store (Redis/DB) with an atomic decrement or optimistic locking. No persistence —
  data is lost on restart.
- **No booking retrieval or cancellation** (out of the stated scope) — bookings are stored but not readable.
- **No idempotency on `POST /bookings`.** A client that retries after a timeout (its first call having
  actually succeeded) would create a second booking and consume seats twice. An `Idempotency-Key` header,
  deduplicated server-side to return the original result, is the classic booking-API safeguard for this.
- **Logbook logs request/response bodies** — handy for a demo, but would leak PII in production; it should
  be filtered/obfuscated or disabled there.
- **No auth / rate limiting** (per the brief) — not production-safe as-is.
- **Custom error shape, not RFC 7807** — fine here, but Zalando Problem would interoperate better.
- **No CI gate** — build/test/coverage/OpenAPI-lint should gate merges; coverage isn't threshold-enforced.
- **Simple seat count** — no seat map, fare classes, or holds/expiry.

See the README's "What I'd improve" for the broader application-wise and infra-wise roadmap.
