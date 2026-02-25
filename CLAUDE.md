# CLAUDE.md — Handify

This file provides guidance to Claude Code when working with this repository.
Read this file completely before making any changes to the codebase.

---

## Project Overview

Handify is a **Kotlin Multiplatform (KMP)** marketplace platform where clients post jobs and contractors place bids.

**Modules:**
- `composeApp/` — Android UI (Jetpack Compose), Android entry point
- `iosApp/` — iOS UI (SwiftUI in Xcode), iOS entry point
- `shared/` — Business logic, domain, data layer, ViewModels — shared between Android and iOS
- `server/` — Ktor backend server (JVM/Netty), runs on port 8080

**Package root:** `com.example.handify`

---

## Tech Stack

### Shared (commonMain)
| Library | Purpose |
|---------|---------|
| Ktor Client 3.3.3 | HTTP calls to server |
| kotlinx.serialization | JSON parsing |
| Koin 4.0.0 | Dependency Injection |
| androidx.lifecycle ViewModel | ViewModels in commonMain |
| kotlinx.coroutines | async/await |

### composeApp (Android)
| Library | Purpose |
|---------|---------|
| Jetpack Compose BOM | UI |
| Navigation Compose | Navigation |
| Koin Android | DI for Android |
| Material3 | Design system |

### server
| Library | Purpose |
|---------|---------|
| Ktor Server 3.3.3 | Web framework (Netty) |
| Exposed | ORM |
| PostgreSQL | Database |
| JWT (java-jwt) | Authentication |
| Koin Ktor | DI for server |

---

## Architecture

This project follows **Clean Architecture**. Every feature must respect the layer boundaries described below. Never skip layers or mix responsibilities.

### The 3 Layers

```
┌──────────────────────────────┐
│       Presentation           │  ViewModels, UI State
├──────────────────────────────┤
│          Domain              │  Models, Use Cases, Repository Interfaces
├──────────────────────────────┤
│           Data               │  Repository Implementations, API, DTOs
└──────────────────────────────┘
```

**Dependency rule — dependencies only point inward:**
- Presentation depends on Domain
- Data depends on Domain
- Domain depends on NOTHING — it is pure Kotlin, no framework imports

### Layer Responsibilities

**Domain layer** (`shared/commonMain/domain/`)
- Contains the core business logic of the application
- Defines `data class` models that represent real-world entities (Job, Bid, User)
- Defines `interface` for every repository — describes WHAT can be done, not HOW
- Contains Use Cases **only when there is real logic** — validation, combining multiple repositories, or complex transformations. Do NOT create a Use Case just to wrap a single repository call.
- Has zero dependencies on Ktor, Android, or any framework

**Data layer** (`shared/commonMain/data/`)
- Implements the repository interfaces defined in domain
- Contains Response models (API responses) annotated with `@Serializable`
- Contains mappers that convert Response models to domain models
- Knows about Ktor Client, but domain does not know about data

**Presentation layer** (`shared/commonMain/presentation/`)
- Contains ViewModels that hold and manage UI state
- Calls Use Cases when real logic exists, otherwise calls repositories directly
- Exposes immutable state to the UI
- Never imports Compose or SwiftUI — it is platform-agnostic

**UI** (`composeApp/androidMain/` and `iosApp/`)
- Observes state from ViewModels
- Sends user actions to ViewModels
- Contains zero business logic
- Screens do not know about navigation — they receive callbacks

### Server Architecture

The server follows **feature-based + Clean Architecture (light)**. Each feature is self-contained with its own `api/`, `domain/`, and `data/` layers.

```
features/
└── job/
    ├── api/      ← Routes, Request/Response models (HTTP layer)
    ├── domain/   ← Models, Repository interface, Use Cases
    └── data/     ← Repository implementation, Table, Queries
```

**Unlike shared/ — Use Cases DO make sense on the server** because each feature has real business logic (login, register, place bid, validate job etc.).

**`api/` layer** (`features/<feature>/api/`)
- `<Feature>Routes.kt` — Ktor route definitions, calls Use Cases or Repository
- `<Feature>Request.kt` — `@Serializable` data classes for incoming request bodies
- `<Feature>Response.kt` — `@Serializable` data classes for outgoing responses
- All routes except `/auth/*` are protected with JWT

**`domain/` layer** (`features/<feature>/domain/`)
- Domain model (e.g. `Job.kt`) — pure Kotlin, no framework imports
- Repository interface — describes what the data layer must implement
- Use Cases — contain the real business logic for this feature

**`data/` layer** (`features/<feature>/data/`)
- `<Feature>RepositoryImpl.kt` — implements the repository interface using Exposed
- `<Feature>Table.kt` — Exposed table schema definition
- `<Feature>Queries.kt` (optional) — extracted complex queries

**`app/`** — Ktor bootstrap only: installs plugins, registers all routes, wires DI

**`core/`** — Shared infrastructure across all features: database connection, JWT, error handling, utilities

**How a server request flows:**
```
HTTP Request
    ↓
Ktor Route (features/<feature>/api/<Feature>Routes.kt)
    ↓ receive + validate request
Use Case (features/<feature>/domain/<Action>UseCase.kt)
    ↓ business logic
Repository Interface (features/<feature>/domain/<Feature>Repository.kt)
    ↑ implemented by
Repository Impl (features/<feature>/data/<Feature>RepositoryImpl.kt)
    ↓ dbQuery { }
Exposed DSL + <Feature>Table.kt
    ↓
PostgreSQL
    ↓ ResultRow → domain model → Response
HTTP Response
```

**Naming on the server:**

| Type | Convention | Example |
|------|-----------|---------|
| Request body | `<Action>Request` | `CreateJobRequest`, `LoginRequest` |
| Response body | `<Feature>Response` | `JobResponse`, `AuthResponse` |
| Use Case | `<Verb><Entity>UseCase` | `CreateJobUseCase`, `PlaceBidUseCase` |
| Repository interface | `<Entity>Repository` | `JobRepository` |
| Repository impl | `<Entity>RepositoryImpl` | `JobRepositoryImpl` |
| Table | `<Entity>Table` | `JobTable`, `UserTable` |
| Routes function | `Route.<feature>Routes()` | `fun Route.jobRoutes()` |



```
User action
    ↓
Screen (Composable / SwiftUI View)
    ↓ calls function
ViewModel (shared/presentation)
    ↓ calls
Use Case (shared/domain)
    ↓ calls interface
Repository Interface (shared/domain)
    ↑ implemented by
Repository Impl (shared/data)
    ↓ HTTP request
Ktor Client → Server
    ↓
Ktor Route (server/routing)
    ↓
Server Repository (server/data)
    ↓
Exposed + PostgreSQL
```

---

## Package Structure

### shared/commonMain
```
shared/commonMain/kotlin/com/example/handify/
├── core/
│   ├── di/
│   │   ├── SharedModule.kt
│   │   └── NetworkModule.kt
│   ├── network/
│   │   └── HttpClientFactory.kt
│   └── util/
│       └── Result.kt
├── domain/
│   ├── model/
│   │   ├── Job.kt
│   │   ├── Bid.kt
│   │   └── User.kt
│   ├── repository/
│   │   ├── JobRepository.kt
│   │   ├── BidRepository.kt
│   │   └── AuthRepository.kt
│   └── usecase/
│       ├── job/
│       │   ├── GetJobsUseCase.kt
│       │   ├── GetJobByIdUseCase.kt
│       │   └── PostJobUseCase.kt
│       └── bid/
│           ├── PlaceBidUseCase.kt
│           └── AcceptBidUseCase.kt
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   ├── JobApi.kt
│   │   │   └── BidApi.kt
│   │   ├── response/
│   │   │   ├── JobResponse.kt
│   │   │   └── BidResponse.kt
│   │   └── mapper/
│   │       ├── JobMapper.kt
│   │       └── BidMapper.kt
│   └── repository/
│       ├── JobRepositoryImpl.kt
│       └── BidRepositoryImpl.kt
└── presentation/
    ├── job/
    │   ├── JobListViewModel.kt
    │   ├── JobListState.kt
    │   ├── JobDetailViewModel.kt
    │   └── JobDetailState.kt
    └── bid/
        ├── PlaceBidViewModel.kt
        └── PlaceBidState.kt
```

### composeApp/androidMain
```
composeApp/androidMain/kotlin/com/example/handify/
├── ui/
│   ├── screen/
│   │   ├── JobListScreen.kt
│   │   ├── JobDetailScreen.kt
│   │   ├── CreateJobScreen.kt
│   │   └── PlaceBidScreen.kt
│   ├── component/
│   │   ├── JobCard.kt
│   │   └── BidCard.kt
│   └── theme/
│       ├── Theme.kt
│       ├── Color.kt
│       └── Type.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── Routes.kt
├── di/
│   └── AppModule.kt
└── MainActivity.kt
```

### server/main
```
server/main/kotlin/com/example/handify/
├── app/
│   ├── Application.kt              ← bootstrap, install plugins, register routes
│   ├── plugins/
│   │   ├── Routing.kt
│   │   ├── Security.kt
│   │   ├── Serialization.kt
│   │   └── StatusPages.kt
│   └── di/
│       └── ServerModule.kt
│
├── core/
│   ├── database/
│   │   ├── DatabaseFactory.kt
│   │   └── Transaction.kt          ← dbQuery { } helper
│   ├── security/
│   │   ├── JwtConfig.kt
│   │   └── password/
│   ├── errors/
│   │   ├── ApiException.kt
│   │   └── ErrorResponse.kt
│   └── util/
│       └── Id.kt
│
├── features/
│   ├── job/
│   │   ├── api/
│   │   │   ├── JobRoutes.kt
│   │   │   ├── JobRequest.kt
│   │   │   └── JobResponse.kt
│   │   ├── domain/
│   │   │   ├── Job.kt
│   │   │   ├── JobRepository.kt    ← interface
│   │   │   ├── CreateJobUseCase.kt
│   │   │   └── ListJobsUseCase.kt
│   │   └── data/
│   │       ├── JobRepositoryImpl.kt
│   │       ├── JobTable.kt
│   │       └── JobQueries.kt
│   ├── bid/
│   │   ├── api/
│   │   │   ├── BidRoutes.kt
│   │   │   ├── BidRequest.kt
│   │   │   └── BidResponse.kt
│   │   ├── domain/
│   │   │   ├── Bid.kt
│   │   │   ├── BidRepository.kt
│   │   │   ├── PlaceBidUseCase.kt
│   │   │   └── ListBidsUseCase.kt
│   │   └── data/
│   │       ├── BidRepositoryImpl.kt
│   │       ├── BidTable.kt
│   │       └── BidQueries.kt
│   └── auth/
│       ├── api/
│       │   ├── AuthRoutes.kt
│       │   ├── LoginRequest.kt
│       │   └── AuthResponse.kt
│       ├── domain/
│       │   ├── User.kt
│       │   ├── UserRepository.kt
│       │   ├── LoginUseCase.kt
│       │   └── RegisterUseCase.kt
│       └── data/
│           ├── UserRepositoryImpl.kt
│           ├── UserTable.kt
│           └── UserQueries.kt
│
└── Application.kt                  ← entrypoint only
```

---

## Naming Conventions

### Files & Classes
| Type | Convention | Example |
|------|-----------|---------|
| Domain model | `PascalCase` | `Job.kt`, `Bid.kt` |
| Repository interface | `<Entity>Repository` | `JobRepository.kt` |
| Repository implementation | `<Entity>RepositoryImpl` | `JobRepositoryImpl.kt` |
| Use Case | `<Verb><Entity>UseCase` | `GetJobsUseCase.kt`, `PlaceBidUseCase.kt` |
| ViewModel | `<Feature>ViewModel` | `JobListViewModel.kt` |
| UI State | `<Feature>State` | `JobListState.kt` |
| API Response model | `<Entity>Response` | `JobResponse.kt`, `BidResponse.kt` |
| Mapper | `<Entity>Mapper` | `JobMapper.kt` |
| Screen (Composable) | `<Feature>Screen` | `JobListScreen.kt` |
| Component (Composable) | `<Entity>Card`, `<Name>Button` | `JobCard.kt` |
| Exposed Table | `<Entity>sTable` | `JobsTable.kt` |
| Ktor Routes | `<Entity>Routes` | `JobRoutes.kt` |
| Koin Module | `<scope>Module` | `sharedModule`, `appModule` |

### Functions & Variables
| Type | Convention | Example |
|------|-----------|---------|
| Functions | `camelCase` | `loadJobs()`, `placeBid()` |
| Use Case invoke | `operator fun invoke` | `operator fun invoke(): List<Job>` |
| State properties | `camelCase` | `isLoading`, `errorMessage` |
| Constants | `SCREAMING_SNAKE_CASE` | `SERVER_PORT`, `BASE_URL` |
| Compose functions | `PascalCase` | `JobCard()`, `LoadingIndicator()` |
| API endpoints | `kebab-case` | `/api/jobs`, `/api/place-bid` |
| DB table names | `snake_case` | `jobs`, `bids`, `users` |
| DB column names | `snake_case` | `client_id`, `created_at` |

### Specific Patterns

**Use Cases — always use `operator fun invoke`:**
```kotlin
class GetJobsUseCase(private val repository: JobRepository) {
    suspend operator fun invoke(): List<Job> = repository.getJobs()
}

// Called like a function:
val jobs = getJobs()
```

**ViewModels — call Use Cases only when logic exists, otherwise call repository directly:**
```kotlin
// Simple case — no use case needed, call repository directly
class JobListViewModel(
    private val jobRepository: JobRepository
) : ViewModel() {

    var state by mutableStateOf(JobListState())
        private set

    fun loadJobs() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = state.copy(jobs = jobRepository.getJobs(), isLoading = false)
        }
    }
}

// Complex case — use case makes sense (combines repo + validation + logic)
class PlaceBidViewModel(
    private val placeBidUseCase: PlaceBidUseCase  // validates amount, checks job status, etc.
) : ViewModel() {

    fun placeBid(jobId: String, amount: Double) {
        viewModelScope.launch {
            placeBidUseCase(jobId, amount)
        }
    }
}

data class JobListState(
    val jobs: List<Job> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

**Screens — receive callbacks, never NavController:**
```kotlin
@Composable
fun JobListScreen(
    onJobClick: (String) -> Unit,
    onCreateJobClick: () -> Unit,
    viewModel: JobListViewModel = koinViewModel()
) {
    val state = viewModel.state
    // UI only
}
```

**Repository Interface — suspend functions, domain models only:**
```kotlin
interface JobRepository {
    suspend fun getJobs(): List<Job>
    suspend fun getJobById(id: String): Job?
    suspend fun createJob(title: String, description: String, budget: Double): Job
}
```

**API Response models — annotated with @Serializable, separate from domain models:**
```kotlin
@Serializable
data class JobResponse(
    val id: String,
    val title: String,
    val description: String,
    val budget: Double,
    val clientId: String,
    val status: String,
    val createdAt: Long
)

// Mapper converts Response → domain model
fun JobResponse.toDomain() = Job(
    id = id,
    title = title,
    description = description,
    budget = budget,
    clientId = clientId,
    status = JobStatus.valueOf(status),
    createdAt = createdAt
)
```

**Ktor Routes — always authenticated except /auth:**
```kotlin
fun Route.jobRoutes(repository: JobRepository) {
    authenticate("jwt") {
        route("/api/jobs") {
            get { call.respond(repository.getJobs()) }
            post {
                val request = call.receive<CreateJobRequest>()
                call.respond(HttpStatusCode.Created, repository.createJob(request))
            }
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val job = repository.getJobById(id) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(job)
            }
        }
    }
}
```

---

## Rules — Never Break These

1. **Domain has zero framework dependencies** — no Ktor, no Android, no Compose imports in `domain/`
2. **Screens never hold business logic** — all logic lives in ViewModels
3. **Screens never receive NavController** — they receive lambdas as callbacks
4. **Do NOT create a Use Case just to wrap a single repository call** — call the repository directly from the ViewModel in that case
5. **Create a Use Case only when there is real logic** — validation, combining multiple repositories, or complex data transformations
6. **Never put UI code in shared** — shared must compile on all platforms
7. **DTOs never leave the data layer** — always map to domain models before returning
8. **State is immutable** — always use `copy()` to update state, never mutate directly
9. **No comments in code** — write self-explanatory code; do not add any inline comments, block comments, or KDoc
10. **No AI slop** — no filler phrases, no over-engineering, no unnecessary abstractions, no placeholder text, no TODO comments

---

## Order of Implementation for a New Feature

Always follow this order. Never start with UI before the domain exists.

```
1. shared/domain/model/                      → add or update domain model
2. shared/domain/repository/                 → add method to repository interface
3. shared/domain/usecase/                    → create use case ONLY if real logic is needed
4. shared/data/response/                     → add Response model (@Serializable)
5. shared/data/remote/api/                   → add API call
6. shared/data/mapper/                       → add mapper Response → domain
7. shared/data/repository/                   → implement repository method (client side)
8. shared/presentation/                      → add ViewModel + State
9. server/features/<feature>/domain/         → add domain model + repository interface + use case
10. server/features/<feature>/data/          → implement repository + table + queries
11. server/features/<feature>/api/           → add routes + request/response models
12. composeApp/ui/screen/                    → create Composable screen
13. composeApp/navigation/                   → add route to AppNavigation
14. iosApp/                                  → create SwiftUI view
```

---

## Build & Run Commands

```bash
# Build Android APK
./gradlew :composeApp:assembleDebug

# Run Ktor server
./gradlew :server:run

# Run all tests
./gradlew test

# Run tests per module
./gradlew :shared:test
./gradlew :server:test
./gradlew :composeApp:test

# Run a single test class
./gradlew :server:test --tests "com.example.handify.ApplicationTest"

# Lint
./gradlew lint
```

iOS builds must be done via Xcode — open `iosApp/` in Xcode.

---

## Key Versions
- Kotlin: 2.3.0
- Ktor: 3.3.3
- Koin: 4.0.0
- Android minSdk: 24 / targetSdk: 36
- JVM target: 11

All dependency versions are managed via the version catalog at `gradle/libs.versions.toml`.