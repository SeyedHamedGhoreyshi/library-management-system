# Library Management System

A multi-module Spring Boot application for managing a library catalog — book registration, cataloging/search, and borrow/return workflows — built with Hexagonal (Ports & Adapters) Architecture, CQRS, JWT authentication, and role-based access control.

---

## 1. How to Run the Project

### Option A — Docker Compose (recommended)

This spins up both the MySQL database and the application container.

```bash
# 1. Clone the repository
git clone https://github.com/SeyedHamedGhoreyshi/library-management-system
cd library-management-system
# 2. Create your environment file
cp .env.example .env
# Edit .env and set DB_PASSWORD, MYSQL_ROOT_PASSWORD, and JWT_SECRET
# Generate a JWT secret with:
openssl rand -base64 64

# 3. Build and start the stack
# --env-file is required so Compose can substitute ${DB_NAME}, ${DB_USERNAME}, etc.
# in dockers/docker-compose.yml from the root .env file (env_file: only injects
# vars into the app container, it does not feed Compose's own substitution).
docker compose --env-file .env -f dockers/docker-compose.yml up --build
```

The API will be available at `http://localhost:8080` 

### Verifying it's running

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`


## 2. Prerequisites

| Tool | Version | Notes                                                                  |
|---|---|------------------------------------------------------------------------|
| **JDK** | 21 | Bytecode target for all modules (Docker image uses Amazon Corretto 21) |
| **Maven** | 3.9+ | Or use the bundled `./mvnw` wrapper — no local install required        |
| **Docker & Docker Compose** | recent | For running MySQL and the full containerized stack                       |
| **MySQL** | 8.0 | Only needed if not using Docker Compose                                |


Before the first run, copy `.env.example` to `.env` and fill in:
- `DB_USERNAME` / `DB_PASSWORD` / `MYSQL_ROOT_PASSWORD`
- `JWT_SECRET` (generate with `openssl rand -base64 64`)

---

## 3. Technologies Used

**Core Framework**
- Java 21
- Spring Boot 3.5 (`spring-boot-starter-parent`)
- Maven (multi-module reactor build)

**Web / API**
- Spring MVC (`spring-boot-starter-web`)
- springdoc-openapi (Swagger UI + OpenAPI 3 docs)
- Jakarta Bean Validation (`spring-boot-starter-validation`)

**Security**
- Spring Security 6 (stateless, filter-chain based)
- JJWT (`io.jsonwebtoken`) for JWT issuing/validation
- BCrypt password hashing

**Persistence**
- Spring Data JPA / Hibernate
- MySQL 8 (`mysql-connector-j`)
- JPA `Specification` API for dynamic, composable query filtering
- Optimistic locking via `@Version`

**Utilities & Tooling**
- Lombok (compile-time boilerplate reduction in the infrastructure modules)
- `spring-dotenv` (loads `.env` into the Spring `Environment` for local dev)

**Testing**
- JUnit 5, Mockito, AssertJ (`spring-boot-starter-test`)

**Containerization**
- Docker (multi-stage build: Maven/Corretto builder → minimal Corretto 21 headless runtime)
- Docker Compose (app + MySQL with health-checked startup ordering)

---

## 4. Project Structure

The project is a Maven multi-module reactor implementing **Hexagonal (Ports & Adapters) Architecture**. Dependencies only ever point *inward*, toward the domain.

```
library-management-system/
├── pom.xml                          # Parent reactor POM (module list, shared Java/Lombok config)
├── Dockerfile                       # Multi-stage build: Maven builder → slim runtime image
├── dockers/
│   └── docker-compose.yml           # App + MySQL orchestration with healthchecks
├── .env.example                     # Template for required environment variables
│
├── core/                            # ─── Application core (framework-agnostic) ───
│   ├── domain/                      # Innermost layer — pure Java, zero framework deps
│   │   └── src/main/java/com/library/core/domain/
│   │       ├── model/                # Book (aggregate root), BorrowRecord (value object), Role
│   │       └── exception/            # DomainException hierarchy
│   │
│   └── application/                 # Use-case orchestration layer
│       └── src/main/java/com/library/core/application/
│           ├── port/
│           │   ├── input/
│           │   │   ├── usecase/      # Inbound ports (RegisterBookUseCase, BorrowBookUseCase, ...)
│           │   │   ├── command/      # CQRS write models (RegisterBookCommand, ...)
│           │   │   ├── query/        # CQRS read models (GetBooksQuery, ...)
│           │   │   └── result/       # Read-side DTOs (BookResult, PageResult, ...)
│           │   ├── output/           # Outbound/driven ports (BookRepository, BorrowRecordRepository)
│           │   └── exception/        # Application-layer exceptions (BookNotFoundException, ...)
│           └── service/              # Use-case implementations (BookCommandService, BorrowService, ...)
│
├── infrastructure/                  # ─── Adapters (framework-specific) ───
│   ├── mysql/                       # Persistence adapter
│   │   └── src/main/java/com/library/infrastructure/mysql/
│   │       ├── entity/               # JPA entities (BookEntity, BorrowRecordEntity)
│   │       ├── repository/           # Spring Data repositories + BookSpecification (dynamic filtering)
│   │       ├── mapper/               # Domain <-> JPA entity mapping
│   │       └── adapter/              # Implements the application's output ports
│   │
│   ├── security/                    # Authentication/authorization adapter
│   │   └── src/main/java/com/library/infrastructure/security/
│   │       ├── access/               # UserInfo entity + repository (isolated auth schema)
│   │       ├── config/               # SecurityConfig, AuthenticationConfig, PasswordEncoderConfig
│   │       ├── filter/                # JwtAuthFilter (stateless JWT verification)
│   │       └── service/               # JwtService, UserInfoService, UserInfoDetails
│   │
│   └── rest/                        # Inbound web adapter
│       └── src/main/java/com/library/infrastructure/web/
│           ├── controller/           # AuthController, BookController, BorrowController
│           ├── dto/request|response/ # Transport DTOs (never expose domain/entities directly)
│           ├── exception/            # GlobalExceptionHandler (@RestControllerAdvice)
│           ├── security/             # SecurityUtils (extracts Role from SecurityContext)
│           └── config/               # OpenApiConfig
│
└── bootstrap/                       # ─── Composition root ───
    └── src/main/java/com/library/bootstrap/
        ├── LibraryManagementApplication.java  # @SpringBootApplication entry point
        └── BootstrapConfig.java               # Wires all module @Configuration classes together
```

---

## 5. Three Important Design Decisions

### 5.1 Hexagonal Architecture enforced through Maven module boundaries

Rather than relying on package-naming conventions alone, architectural layering is enforced at the **build level**: `core/domain` is a plain Maven module with *no* Spring (or any framework) dependency at all — no `@Component`, `@Entity`, or `@NotNull` is physically compilable there. `core/application` depends only on `domain` plus `spring-context`/`spring-tx` for transactional orchestration. The three `infrastructure/*` modules (`mysql`, `security`, `rest`) each depend inward on `application`/`domain`, never on each other directly except where `rest` legitimately consumes `security`'s authentication services. `bootstrap` is the only module aware of every other module — it exists solely to wire configuration classes together (`BootstrapConfig`) and hold the `@SpringBootApplication` entry point.

**Why this matters:** a misplaced dependency (e.g. a domain class importing `jakarta.persistence.Entity`) becomes a *compile error*, not a code-review nitpick. This makes the domain model genuinely portable and unit-testable in complete isolation — `core/domain` has its own test suite (`BookTest`, `BorrowRecordTest`) that runs without a Spring context, a database, or any mocking framework, exercising pure business logic (ISBN validation, borrow/return state transitions, soft-delete rules) directly.

### 5.2 CQRS-style separation of commands, queries, and results in the application layer

The application layer explicitly splits its input ports into three concerns: `command` (write intents like `RegisterBookCommand`, `BorrowBookCommand`), `query` (read intents like `GetBooksQuery`), and `result` (read models like `BookResult`, `PageResult<T>`). Each use case is a narrow, single-method interface (`RegisterBookUseCase`, `BorrowBookUseCase`, `GetBooksUseCase`, ...) rather than one large `BookService` interface, and is implemented by focused services (`BookCommandService`, `BookQueryService`, `BorrowService`).

**Why this matters:** write paths and read paths evolve independently. The read side can add filtering/sorting/pagination capabilities (see `BookSpecification`, a JPA `Specification` builder that composes predicates dynamically from `GetBooksQuery`) without touching write-side validation logic, and vice versa. It also keeps controllers thin and declarative — `BookController` simply maps HTTP DTOs to Command/Query records and delegates, with all business rules living in the domain and orchestration living in the application services (each transactionally bounded).

### 5.3 Rich domain model with static factories and self-validating invariants, decoupled from persistence identity

`Book` is not an anemic data holder — its constructor is private, and construction happens exclusively through two static factories: `register(...)` (for genuinely new books with no ID yet) and `reconstitute(...)` (for rebuilding a `Book` from persisted state, optionally carrying its optimistic-locking `version`). All mutating behavior (`borrow`, `returnBook`, `softDelete`, `updateDetails`) lives on the aggregate itself and throws domain-specific exceptions (`BookNotAvailableException`, `BookIsBorrowedException`, `InvalidDomainStateException`, `UnauthorizedDomainActionException`) when an invariant is violated — e.g. you cannot borrow an unpersisted book, return a book that isn't out on loan, or delete a book that's currently borrowed. `BorrowRecord` is modeled as an immutable value object: returning a book doesn't mutate the record in place, it produces a new instance via `withReturnDate(...)`.

**Why this matters:** business rules can't be bypassed by infrastructure code, because the only way to get a `Book` into an invalid state is through the aggregate's own methods, and those methods refuse to do it. The persistence adapter (`BookMapper`, `BookRepositoryAdapter`) is responsible for translating between this rich domain object and the mutable `BookEntity` JPA record, and the domain never leaks a JPA-managed identity or proxy back through the port boundary. The `GlobalExceptionHandler` then maps each domain/application exception to its documented HTTP status (404 / 400 / 422 / 403 / 409), keeping error semantics consistent with the API contract regardless of which use case triggered them.

---

## 6. Future Improvements

- **Testcontainers-based integration tests** — current test coverage is strong at the domain layer (`core/domain`) but the persistence, security, and REST adapters lack integration tests against a real (containerized) MySQL instance; this would catch mapping/query regressions that unit tests and mocks can't.
- **Refresh tokens & token revocation** — the current JWT flow issues a single short-lived access token with no refresh mechanism or server-side revocation/blacklist, so a compromised token remains valid until expiry.
- **Centralized API versioning strategy** beyond the current `/api/v1` prefix, to document deprecation paths as the contract evolves.
- **Structured logging & observability** (correlation IDs, Micrometer/Prometheus metrics, tracing) — the dependency tree includes observability-related libraries transitively but nothing is wired up yet. .
