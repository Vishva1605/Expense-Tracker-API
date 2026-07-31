# AI Notes

## Which parts were AI-generated vs. written by me

I used Claude to generate the initial implementation of this project: the Maven `pom.xml`, the Spring Boot application structure (controller/service/repository/model/dto/exception layers), and the JUnit/MockMvc test suite. I chose the tech stack (Java + Spring Boot + Maven + Swagger) myself, matching what I already use — this is the same stack as my Job Portal Backend System project.

**AI-generated, then reviewed by me:**
- `pom.xml` dependency set (Spring Web, Validation, springdoc-openapi, Spring Boot Test)
- Layered structure: `Expense` model, `ExpenseRepository` (in-memory `ConcurrentHashMap`), `ExpenseService`, `ExpenseController`
- Bean validation on `ExpenseRequest` (`@NotBlank`, `@Positive`, `@NotNull`)
- Global exception handling (`GlobalExceptionHandler`) for 404s and validation errors
- Unit tests (`ExpenseServiceTest`) and MockMvc integration tests (`ExpenseControllerTest`)
- Swagger/OpenAPI annotations on the controller

**Written / decided by me:**
- Which endpoints and response shapes to expose (matching the assignment's exact requirements)
- The decision to sort expenses by date descending by default
- The decision to make category filtering case-insensitive
- The decision to return `0.0` (not a 404) for a category total with no expenses

## What I validated, tested, or changed, and why

*(This section needs to be filled in with what I actually did after building and running the project locally — see "Before I submit this" below.)*

Things to specifically check and note here once I've run it:
- Confirm `mvn clean install` succeeds on a clean checkout and all tests in `ExpenseServiceTest` / `ExpenseControllerTest` pass
- Confirm the Swagger UI loads at `/swagger-ui.html` and every endpoint is documented correctly
- Walk through the curl examples in the README myself and confirm the responses match
- Double check the validation error messages actually look right for a blank title / negative amount / missing date
- Note here if I changed anything from the AI's first draft — e.g. if I disagreed with the sort order, the UUID choice, the exception handling approach, or anything else

## AI suggestions I decided not to use, and why

*(To be filled in — for example, if Claude suggested Lombok, an H2 database, a `.gitignore`/CI pipeline, or JSON-file persistence and I decided a plain in-memory map with explicit getters/setters was simpler and easier to reason about for a 4-hour assignment, that reasoning goes here.)*

---

**Note to reviewer:** this file was scaffolded with AI help alongside the code. Per the assignment instructions, I'm keeping the sections above so I can fill them in with my own specific validation notes after actually building, running, and testing the project on my machine — a generic AI_NOTES isn't the point of this exercise.
