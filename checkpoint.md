# checkpoint.md — Progress & Blocking Questions

> **Role of this file:** running progress log (per phase) plus any blocking questions raised to the user.
> Updated at the end of every phase. If the agent gets stuck on a roadmap decision, it stops and logs
> the question here before asking.

- **Last updated:** 2026-07-08
- **Overall status:** 🟢 Implementation started (plan approved)

---

## Phase status

| Phase | Description | Status |
|-------|-------------|--------|
| 0 | Plan authored + approved | ✅ Done |
| 1 | Prompt audit + handover docs; verify env; git init + `.gitignore` | 🔄 In progress |
| 2 | Domain model + in-memory repositories | ⬜ Pending |
| 3 | Services + overbooking logic | ⬜ Pending |
| 4 | DTOs + validation + exceptions + error codes + global handler | ⬜ Pending |
| 5 | Controllers | ⬜ Pending |
| 6 | DataInitializer seed + OpenAPI/Swagger + port 8081 | ⬜ Pending |
| 7 | Logback rolling logs (7-day TTL) | ⬜ Pending |
| 8 | Unit + integration tests + JaCoCo | ⬜ Pending |
| 9 | Postman collection + Newman run | ⬜ Pending |
| 10 | Zip utility (ProjectArchiver) | ⬜ Pending |
| 11 | Dockerfile + .dockerignore | ⬜ Pending |
| 12 | README (app-wise + infra-wise improvements) | ⬜ Pending |
| 13 | Finalize docs + manual-improvement commit + push steps | ⬜ Pending |

---

## Commit log (local, not pushed)
_Recorded as commits are made; each references its driving prompt in `user-input-prompts.txt`._

| # | Commit subject | Driving prompt |
|---|----------------|----------------|
| — | (pending first commit) | — |

---

## Definition of Done checklist
- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Overbooking prevention implemented & verified (incl. concurrency test)
- [ ] Required business lines covered (JaCoCo report)
- [ ] App runs on port 8081; Swagger UI + OpenAPI reachable
- [ ] Postman collection created; Newman run succeeds
- [ ] Rolling logs produced in `logs/` with 7-day retention config
- [ ] Zip utility produces a clean archive
- [ ] Dockerfile builds & runs
- [ ] Docs complete: plan.md, memory.md, checkpoint.md, user-input-prompts.txt, README.md
- [ ] Git: one meaningful commit per step (prompt referenced) + final manual-improvement commit

---

## Blocking questions to user
_None open._ (D1–D6 resolved; plan approved.)

## Notes / risks
- Environment verified (2026-07-08 10:46): Java 17.0.19 LTS, Git 2.55, Node v24.15/npm 11.6, Gradle (scoop), Docker present.
- Newman not installed globally → will run via `npx newman` (no global install needed).
- Target Java 17 toolchain; Spring Boot 3.3.x; springdoc-openapi 2.6.x.
