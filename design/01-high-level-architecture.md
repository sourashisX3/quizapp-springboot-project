# QuizApp — Complete System Architecture & Design

> **Version:** 2.0 (Production-Grade Redesign)
> **Date:** 2026-06-14
> **Status:** Architecture Analysis (Pre-Implementation)
>
> **Phase 0 Scope:** MySQL + Redis + WebSocket + Local File Storage only.
> Paid/cloud services (Elasticsearch, RabbitMQ/Kafka, S3, CDN, managed monitoring) are deferred.

---

## Table of Contents

1. [High-Level Architecture Diagram](#1-high-level-architecture-diagram)
2. [Low-Level Design](#2-low-level-design)
3. [Complete Database Schema](#3-complete-database-schema)
4. [ER Diagram Description](#4-er-diagram-description)
5. [Package Structure](#5-package-structure)
6. [Service Layer Design](#6-service-layer-design)
7. [Repository Layer Design](#7-repository-layer-design)
8. [Security Architecture](#8-security-architecture)
9. [Microservice Migration Plan](#9-microservice-migration-plan)
10. [Redis Usage Plan](#10-redis-usage-plan)
11. [WebSocket Architecture](#11-websocket-architecture)
12. [Future AI Integration Plan](#12-future-ai-integration-plan)
13. [Scalability Bottlenecks and Solutions](#13-scalability-bottlenecks-and-solutions)
14. [Production Deployment Recommendations](#14-production-deployment-recommendations)

---

## 1. High-Level Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT LAYER                                     │
│    Web App (React)    │    Mobile (Android/iOS)    │    3rd Party API        │
└───────────┬──────────────────────────┬──────────────────────────┬────────────┘
            │                          │                          │
            │        HTTPS/WebSocket   │                          │
            ▼                          ▼                          ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                         SECURITY / FILTER LAYER                               │
│                                                                              │
│   ┌────────────────────────────────────────────────────────────────────┐     │
│   │  Spring Security Filter Chain                                      │     │
│   │  • JWT Authentication Filter    • Rate Limiting (in-memory/Redis)  │     │
│   │  • CORS Management             • Request/Response Logging         │     │
│   │  • IP Whitelisting             • CSRF Disabled (stateless JWT)    │     │
│   └────────────────────────────────────────────────────────────────────┘     │
└───────────────────────────┬──────────────────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                         APPLICATION LAYER (MONOLITH)                         │
│                                                                              │
│   ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐         │
│   │  Auth    │ │  User    │ │  Quiz    │ │  Contest  │ │Leaderboard│        │
│   │  Module  │ │  Module  │ │  Module  │ │  Module  │ │  Module  │         │
│   └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘         │
│        │             │            │            │            │                │
│   ┌────┴─────┐ ┌────┴─────┐ ┌────┴─────┐ ┌────┴─────┐ ┌────┴─────┐         │
│   │Notification│ │  File   │ │Analytics │ │  Audit   │ │    AI    │         │
│   │  Module  │ │  Module  │ │  Module  │ │  Module  │ │  Module  │         │
│   └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘         │
│        │             │            │            │            │                │
│   ┌────┴───────────────────────────────────────────────────────┴─────┐      │
│   │                    SHARED INFRASTRUCTURE                          │      │
│   │  • Common Utilities             • Exception Handling             │      │
│   │  • API Response Wrapper          • DTO mappers (MapStruct)       │      │
│   │  • Validation                    • Aspect Oriented Programming   │      │
│   └──────────────────────────────────────────────────────────────────┘      │
└───────────────────────────┬──────────────────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                         DOMAIN / SERVICE LAYER                                │
│                                                                              │
│   ┌────────────────────────────────────────────────────────────────────┐     │
│   │  • Domain Entities          • Domain Services                      │     │
│   │  • Value Objects            • Domain Events                        │     │
│   │  • Repository Interfaces    • Specification Patterns               │     │
│   └────────────────────────────────────────────────────────────────────┘     │
│                                                                              │
│   Commands ──► Command Bus ──► Command Handlers ──► Domain Events ──►       │
│   Queries   ──► Query Bus   ──► Query Handlers                             │
└───────────────────────────┬──────────────────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                        PERSISTENCE LAYER                                      │
│                                                                              │
│   ┌──────────────────────┐  ┌──────────────────────┐                         │
│   │  MySQL 8             │  │   Redis              │                         │
│   │  (Primary DB)        │  │  (Cache + Sorted Sets│                         │
│   │  + Local File System │  │   + Rate Limiting)   │                         │
│   │  (File Storage)      │  │                      │                         │
│   └──────────────────────┘  └──────────────────────┘                         │
│                                                                              │
│   ┌────────────────────────────────────────────────────────────────────┐     │
│   │  • JPA / Hibernate (MySQL)            • Spring Data Redis          │     │
│   │  • MySQL FULLTEXT (search)            • Flyway (DB Migration)      │     │
│   │  • QueryDSL (Type-safe Queries)       • Local file:// storage      │     │
│   └────────────────────────────────────────────────────────────────────┘     │
│                                                                              │
│   [Elasticsearch ── Future]  [S3/MinIO ── Future]                           │
└──────────────────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                      IN-PROCESS EVENT LAYER                                   │
│                                                                              │
│   ┌────────────────────────────────────────────────────────────────────┐     │
│   │  Spring ApplicationEventPublisher + @Async                        │     │
│   │  • Domain Events: QuizCompleted, ContestStarted, BadgeAwarded     │     │
│   │  • In-process event bus (same JVM)                                 │     │
│   │  • Configurable async executor thread pool                         │     │
│   └────────────────────────────────────────────────────────────────────┘     │
│                                                                              │
│   [RabbitMQ / Kafka ── Future (for multi-instance / microservices)]         │
└──────────────────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                        MONITORING (BASIC)                                     │
│                                                                              │
│   ┌────────────────────────────────────────────────────────────────────┐     │
│   │  Spring Boot Actuator          • Logback (file + console)          │     │
│   │  • /actuator/health            • Application logs to files         │     │
│   │  • /actuator/metrics           • Request/response logging filter   │     │
│   │  • /actuator/info              • Performance logging AOP           │     │
│   └────────────────────────────────────────────────────────────────────┘     │
│                                                                              │
│   [Prometheus / Grafana / ELK ── Future]                                     │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Architecture Principles

| Principle | Application |
|-----------|-------------|
| **Single Responsibility** | Each module owns one domain concept |
| **Open/Closed** | Modules extend via plugins/events, not modification |
| **Liskov Substitution** | Repository interfaces allow swap of implementations |
| **Interface Segregation** | Role-specific interfaces (AdminService vs UserService) |
| **Dependency Inversion** | High-level modules depend on abstractions (interfaces) |
| **Domain-Driven Design** | Ubiquitous language, bounded contexts, aggregates |
| **Hexagonal Architecture** | Core domain isolated from infrastructure; ports & adapters |
| **CQRS Readiness** | Separate command/query paths at the service level |
| **Event-Driven Readiness** | Domain events published for cross-module communication |

---

## 2. Low-Level Design

### 2.1 Module Interaction Flow

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│   Auth   │────►│   User   │────►│   Quiz   │────►│  Contest │
│  Module  │     │  Module  │     │  Module  │     │  Module  │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
      │               │               │               │
      │               │               │               │
      ▼               ▼               ▼               ▼
┌──────────────────────────────────────────────────────────────┐
│                    Domain Events Bus                          │
│                                                              │
│  Events: UserRegistered │ QuizCompleted │ ContestStarted     │
│          BadgeAwarded   │ LeaderboardUpdated │ XPEarned      │
└──────────────────────────────────────────────────────────────┘
      │               │               │               │
      ▼               ▼               ▼               ▼
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│Notification│   │Leaderboard│    │  Audit   │     │   AI     │
│  Module  │     │  Module  │     │  Module  │     │  Module  │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
```

### 2.2 Request Flow (Example: Submit Quiz)

```
Client ──POST /api/quizzes/{id}/submit──► API Gateway
                                              │
                                         JWT Filter
                                         (Validate Token)
                                              │
                                    ┌─────────▼─────────┐
                                    │ @PreAuthorize      │
                                    │ ("quiz:attempt")   │
                                    └─────────┬─────────┘
                                              │
                                    ┌─────────▼─────────┐
                                    │ QuizController     │
                                    └─────────┬─────────┘
                                              │
                                    ┌─────────▼─────────┐
                                    │ QuizApplicationService│
                                    │ (Command Handler)  │
                                    └─────────┬─────────┘
                                              │
                                    ┌─────────▼─────────┐
                                    │ QuizDomainService  │
                                    │ • Validate answers │
                                    │ • Calculate score  │
                                    │ • Update stats     │
                                    └─────────┬─────────┘
                                              │
                              ┌───────────────┼───────────────┐
                              │               │               │
                              ▼               ▼               ▼
                       ┌──────────┐   ┌──────────┐   ┌──────────┐
                       │  Save    │   │Publish   │   │  Update  │
                       │ QuizAttempt│  │QuizCompleted│  │Leaderboard│
                       │  (MySQL) │   │  (Event)  │   │  (Redis) │
                       └──────────┘   └────┬─────┘   └──────────┘
                                            │
                              ┌─────────────┼─────────────┐
                              │             │             │
                              ▼             ▼             ▼
                       ┌──────────┐  ┌──────────┐  ┌──────────┐
                       │Notification│  │  Award   │  │Analytics │
                       │  Service │  │  Badges  │  │  Update  │
                       └──────────┘  └──────────┘  └──────────┘
```

### 2.3 Layered Architecture per Module (Hexagonal Style)

```
┌──────────────────────────────────────────────────────────────────┐
│                    ADAPTERS (Inbound)                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  REST       │  │  GraphQL    │  │  WebSocket  │              │
│  │  Controller │  │  Resolver   │  │  Handler    │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
├──────────────────────────────────────────────────────────────────┤
│                    APPLICATION LAYER                              │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  • Application Services (Orchestration)                    │  │
│  │  • DTOs, Mappers (MapStruct)                               │  │
│  │  • Command / Query objects                                 │  │
│  │  • Event Publishers                                        │  │
│  └────────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────────┤
│                    DOMAIN LAYER (CORE)                            │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  • Domain Entities         • Value Objects                 │  │
│  │  • Domain Services         • Domain Events                 │  │
│  │  • Repository Interfaces   • Specifications                │  │
│  │  • Business Rules / Invariants                             │  │
│  └────────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────────┤
│                    ADAPTERS (Outbound)                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  JPA        │  │  Redis      │  │  REST Client│              │
│  │  Repository │  │  Repository │  │  (Feign)    │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└──────────────────────────────────────────────────────────────────┘
```

---

## 3. Complete Database Schema

### 3.1 Domain: Authentication & Identity

#### `users`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Unique user ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Public identifier (UUID v4) |
| username | VARCHAR(50) | UNIQUE, NOT NULL, INDEX | Login username |
| email | VARCHAR(255) | UNIQUE, NOT NULL, INDEX | Email address |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hash |
| display_name | VARCHAR(100) | | Display name |
| phone_number | VARCHAR(20) | | Contact phone |
| address | TEXT | | Physical address |
| profile_picture_url | VARCHAR(500) | | Local path under `uploads/` |
| account_status | ENUM('ACTIVE','LOCKED','DISABLED','DELETED') | NOT NULL, DEFAULT 'ACTIVE' | Account status |
| email_verified | BOOLEAN | NOT NULL, DEFAULT FALSE | Email verification flag |
| last_login_at | TIMESTAMP | | Last login timestamp |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Updated timestamp |
| version | INT | NOT NULL, DEFAULT 0 | Optimistic locking |

#### `refresh_tokens`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Token ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | Owner |
| token_hash | VARCHAR(64) | UNIQUE, NOT NULL, INDEX | SHA-256 hash of token |
| expires_at | TIMESTAMP | NOT NULL | Expiry timestamp |
| revoked | BOOLEAN | NOT NULL, DEFAULT FALSE | Revocation flag |
| revoked_at | TIMESTAMP | | When revoked |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Created timestamp |

### 3.2 Domain: RBAC & Permissions

#### `roles`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Role ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL, INDEX | e.g. `ROLE_ADMIN` |
| description | VARCHAR(255) | | Human-readable |
| is_system | BOOLEAN | NOT NULL, DEFAULT FALSE | System-protected role |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL | Updated timestamp |

#### `permissions`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Permission ID |
| name | VARCHAR(100) | UNIQUE, NOT NULL, INDEX | e.g. `quiz:create` |
| resource | VARCHAR(50) | NOT NULL, INDEX | e.g. `quiz` |
| action | VARCHAR(50) | NOT NULL | e.g. `create` |
| description | VARCHAR(255) | | Human-readable |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

> **Note:** `resource` + `action` columns enable programmatic permission generation and fine-grained filtering. `name` is `resource:action` composite.

#### `role_permissions`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| role_id | BIGINT UNSIGNED | FK → roles(id), PK (composite) | Role |
| permission_id | BIGINT UNSIGNED | FK → permissions(id), PK (composite) | Permission |

#### `user_roles`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGINT UNSIGNED | FK → users(id), PK (composite), INDEX | User |
| role_id | BIGINT UNSIGNED | FK → roles(id), PK (composite), INDEX | Role |

**Design Decision:** Many-to-many between users and roles (not single role per user). This enables composite roles (e.g., user can be both `ROLE_MODERATOR` and `ROLE_CONTENT_CREATOR`).

#### `user_permissions` (Direct Permissions Override)
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGINT UNSIGNED | FK → users(id), PK (composite) | User |
| permission_id | BIGINT UNSIGNED | FK → permissions(id), PK (composite) | Permission |
| grant_type | ENUM('GRANT','DENY') | NOT NULL, DEFAULT 'GRANT' | Explicit allow/deny |

**Rationale:** Enables per-user permission exceptions without creating special roles. `DENY` type allows explicit blocking even if a role grants the permission.

### 3.3 Domain: Quiz & Questions

#### `categories`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Category ID |
| name | VARCHAR(100) | UNIQUE, NOT NULL, INDEX | Category name |
| description | VARCHAR(500) | | Description |
| icon_url | VARCHAR(500) | | Icon/image |
| parent_id | BIGINT UNSIGNED | FK → categories(id), NULL | Hierarchical parent |
| sort_order | INT | DEFAULT 0 | Display order |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Soft enable/disable |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL | Updated timestamp |

#### `questions`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Question ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Public identifier |
| category_id | BIGINT UNSIGNED | FK → categories(id), NOT NULL, INDEX | Category |
| title | TEXT | NOT NULL | Question text |
| explanation | TEXT | | Post-answer explanation |
| difficulty | ENUM('EASY','MEDIUM','HARD','EXPERT') | NOT NULL, INDEX | Difficulty level |
| question_type | ENUM('MULTIPLE_CHOICE','TRUE_FALSE','FILL_BLANK','MATCHING') | NOT NULL, DEFAULT 'MULTIPLE_CHOICE' | Question format |
| time_limit_seconds | INT | DEFAULT 30 | Time per question |
| points | INT | NOT NULL, DEFAULT 10 | Base points |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Soft delete |
| tags | VARCHAR(500) | | Comma-separated tags |
| metadata | JSON | | Flexible metadata for future use |
| created_by | BIGINT UNSIGNED | FK → users(id) | Creator |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL | Updated timestamp |

**Indexes:** Composite index on (category_id, difficulty), Fulltext index on (title)

#### `question_options`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Option ID |
| question_id | BIGINT UNSIGNED | FK → questions(id), NOT NULL, INDEX | Parent question |
| option_text | TEXT | NOT NULL | Option text |
| is_correct | BOOLEAN | NOT NULL | Correct answer flag |
| sort_order | INT | NOT NULL | Display order |
| explanation | TEXT | | Per-option explanation |

**Design Decision:** Separate `question_options` table instead of named columns (option1..option4). This supports dynamic number of options, true/false, matching questions, and future question types.

#### `quizzes`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Quiz ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Public identifier |
| title | VARCHAR(255) | NOT NULL | Quiz title |
| description | TEXT | | Description |
| category_id | BIGINT UNSIGNED | FK → categories(id), INDEX | Associated category |
| difficulty | ENUM('EASY','MEDIUM','HARD','EXPERT','MIXED') | NOT NULL, DEFAULT 'MIXED' | Overall difficulty |
| time_limit_minutes | INT | | Total time limit |
| passing_score_pct | DECIMAL(5,2) | DEFAULT 60.00 | Pass threshold |
| max_attempts | INT | DEFAULT 0 (unlimited) | Attempt limit |
| is_randomized | BOOLEAN | NOT NULL, DEFAULT TRUE | Randomize questions |
| is_published | BOOLEAN | NOT NULL, DEFAULT FALSE | Published flag |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Soft delete |
| total_questions | INT | NOT NULL, DEFAULT 0 | Denormalized count |
| total_points | INT | NOT NULL, DEFAULT 0 | Denormalized total |
| created_by | BIGINT UNSIGNED | FK → users(id) | Creator |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL | Updated timestamp |

#### `quiz_questions`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Row ID |
| quiz_id | BIGINT UNSIGNED | FK → quizzes(id), NOT NULL, INDEX | Quiz |
| question_id | BIGINT UNSIGNED | FK → questions(id), NOT NULL | Question |
| sort_order | INT | NOT NULL | Within-quiz order |
| points_override | INT | NULL | Per-question point override |

**Unique Constraint:** (quiz_id, question_id)

#### `quiz_attempts`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Attempt ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Public identifier |
| quiz_id | BIGINT UNSIGNED | FK → quizzes(id), NOT NULL, INDEX | Quiz |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | User |
| contest_participant_id | BIGINT UNSIGNED | FK → contest_participants(id), NULL | Contest context |
| started_at | TIMESTAMP | NOT NULL | Start time |
| submitted_at | TIMESTAMP | | Submit time |
| time_taken_seconds | INT | NULL | Duration |
| score | INT | NOT NULL, DEFAULT 0 | Raw score |
| max_score | INT | NOT NULL | Maximum possible |
| score_pct | DECIMAL(5,2) | | Percentage |
| passed | BOOLEAN | NOT NULL | Pass/fail |
| status | ENUM('IN_PROGRESS','COMPLETED','TIMEOUT','ABANDONED') | NOT NULL, DEFAULT 'IN_PROGRESS' | Status |
| answers_json | JSON | NULL | Snapshot of all answers |
| device_info | VARCHAR(500) | | Browser/device metadata |
| ip_address | VARCHAR(45) | | Client IP |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

**Indexes:** Composite on (user_id, quiz_id), (quiz_id, status), (contest_participant_id)

#### `quiz_answers`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Answer ID |
| attempt_id | BIGINT UNSIGNED | FK → quiz_attempts(id), NOT NULL, INDEX | Attempt |
| question_id | BIGINT UNSIGNED | FK → questions(id), NOT NULL | Question |
| selected_option_id | BIGINT UNSIGNED | FK → question_options(id) | Chosen option |
| answer_text | TEXT | | For fill-blank type |
| is_correct | BOOLEAN | NOT NULL | Correctness |
| points_earned | INT | NOT NULL, DEFAULT 0 | Points awarded |
| time_spent_seconds | INT | | Time on this question |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

**Unique Constraint:** (attempt_id, question_id)

### 3.4 Domain: Contests

#### `contests`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Contest ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Public identifier |
| title | VARCHAR(255) | NOT NULL | Contest title |
| description | TEXT | | Description |
| contest_type | ENUM('DAILY','WEEKLY','MONTHLY','SPECIAL') | NOT NULL, INDEX | Frequency |
| category_id | BIGINT UNSIGNED | FK → categories(id), INDEX | Category filter |
| difficulty | ENUM('EASY','MEDIUM','HARD','EXPERT','MIXED') | NOT NULL | Difficulty |
| quiz_id | BIGINT UNSIGNED | FK → quizzes(id) | Associated quiz (optional) |
| num_questions | INT | NOT NULL | Question count |
| time_limit_minutes | INT | NOT NULL | Duration |
| starts_at | TIMESTAMP | NOT NULL, INDEX | Start time |
| ends_at | TIMESTAMP | NOT NULL, INDEX | End time |
| max_participants | INT | DEFAULT 0 (unlimited) | Capacity |
| min_score_to_qualify | INT | NULL | Qualification threshold |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active flag |
| rules_json | JSON | | Dynamic rules |
| prize_description | TEXT | | Prize description |
| created_by | BIGINT UNSIGNED | FK → users(id) | Creator |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |
| updated_at | TIMESTAMP | NOT NULL | Updated timestamp |

**Indexes:** Composite on (contest_type, starts_at, ends_at), (starts_at, ends_at, is_active)

#### `contest_participants`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Participant ID |
| contest_id | BIGINT UNSIGNED | FK → contests(id), NOT NULL, INDEX | Contest |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | User |
| quiz_attempt_id | BIGINT UNSIGNED | FK → quiz_attempts(id) | Their attempt |
| score | INT | NOT NULL, DEFAULT 0 | Score |
| time_taken_seconds | INT | NULL | Total time |
| rank | INT | NULL | Final rank |
| percentile | DECIMAL(5,2) | NULL | Percentile |
| status | ENUM('REGISTERED','IN_PROGRESS','COMPLETED','DISQUALIFIED') | NOT NULL, DEFAULT 'REGISTERED' | Status |
| registered_at | TIMESTAMP | NOT NULL | Registration time |
| completed_at | TIMESTAMP | | Completion time |

**Unique Constraints:** (contest_id, user_id)
**Indexes:** (contest_id, score DESC) for leaderboard queries, (contest_id, rank)

#### `contest_leaderboard` (Materialized/Ranked View)
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Row ID |
| contest_id | BIGINT UNSIGNED | FK → contests(id), NOT NULL, INDEX | Contest |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | User |
| score | INT | NOT NULL | Score |
| time_taken_seconds | INT | NULL | Tiebreaker |
| rank | INT | NOT NULL | Computed rank |
| percentile | DECIMAL(5,2) | | Percentile |
| last_updated | TIMESTAMP | NOT NULL | Last calculation |

**Design Decision:** This table is populated by a scheduled job or event after contest end. Live leaderboards use Redis Sorted Sets.

### 3.5 Domain: Leaderboard

#### `leaderboards`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Leaderboard ID |
| leaderboard_type | ENUM('GLOBAL','CATEGORY','WEEKLY','MONTHLY','ALL_TIME') | NOT NULL, INDEX | Type |
| category_id | BIGINT UNSIGNED | FK → categories(id), NULL | Category scope |
| period_start | DATE | | Period start |
| period_end | DATE | | Period end |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active flag |
| refresh_interval_seconds | INT | DEFAULT 300 | Cache refresh |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

#### `leaderboard_entries`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Entry ID |
| leaderboard_id | BIGINT UNSIGNED | FK → leaderboards(id), NOT NULL, INDEX | Leaderboard |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | User |
| score | BIGINT | NOT NULL, INDEX | Cumulative score |
| rank | INT | NOT NULL | Position |
| metadata_json | JSON | | Extra data |
| calculated_at | TIMESTAMP | NOT NULL | Calculation time |

**Unique Constraint:** (leaderboard_id, user_id)
**Index:** (leaderboard_id, rank)

### 3.6 Domain: Rewards, Badges, Achievements

#### `badges`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Badge ID |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Badge name |
| description | VARCHAR(500) | | Description |
| icon_url | VARCHAR(500) | | Badge image |
| badge_type | ENUM('SCORE','STREAK','PARTICIPATION','SPECIAL','RANK') | NOT NULL | Category |
| criteria_json | JSON | NOT NULL | Qualification rules |
| points_reward | INT | DEFAULT 0 | XP reward |
| is_hidden | BOOLEAN | DEFAULT FALSE | Secret badge |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

#### `user_badges`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | User badge ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | User |
| badge_id | BIGINT UNSIGNED | FK → badges(id), NOT NULL | Badge |
| awarded_at | TIMESTAMP | NOT NULL | When awarded |
| context_json | JSON | | How it was earned |

**Unique Constraint:** (user_id, badge_id)

#### `achievements`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Achievement ID |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Achievement name |
| description | VARCHAR(500) | | Description |
| icon_url | VARCHAR(500) | | Icon |
| criteria_type | ENUM('SCORE_THRESHOLD','QUIZ_COUNT','STREAK_DAYS','CONTEST_WINS','PERFECT_SCORE','SPEED_DEMON') | NOT NULL | Criteria type |
| criteria_value | INT | NOT NULL | Threshold value |
| xp_reward | INT | DEFAULT 0 | XP reward |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

#### `user_achievements`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | User achievement ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | User |
| achievement_id | BIGINT UNSIGNED | FK → achievements(id), NOT NULL | Achievement |
| progress | INT | NOT NULL, DEFAULT 0 | Current progress |
| is_completed | BOOLEAN | NOT NULL, DEFAULT FALSE | Completion flag |
| completed_at | TIMESTAMP | | Completion date |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

**Unique Constraint:** (user_id, achievement_id)

#### `rewards`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Reward ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | Recipient |
| reward_type | ENUM('BADGE','ACHIEVEMENT','XP_BONUS','COUPON','TITLE','CUSTOM') | NOT NULL | Type |
| source_type | ENUM('QUIZ','CONTEST','ACHIEVEMENT','ADMIN','SYSTEM') | NOT NULL | Source |
| source_id | BIGINT UNSIGNED | | ID of source entity |
| reward_value | VARCHAR(500) | | Value or reference |
| xp_amount | INT | DEFAULT 0 | XP amount |
| claimed | BOOLEAN | NOT NULL, DEFAULT FALSE | Claimed flag |
| claimed_at | TIMESTAMP | | Claimed timestamp |
| expires_at | TIMESTAMP | | Expiry |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

### 3.7 Domain: Notifications

#### `notifications`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Notification ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Public identifier |
| type | ENUM('SYSTEM','CONTEST_START','CONTEST_END','BADGE_EARNED','ACHIEVEMENT_UNLOCKED','RANK_CHANGE','QUIZ_RESULT','REMINDER','ADMIN_BROADCAST') | NOT NULL, INDEX | Type |
| title | VARCHAR(255) | NOT NULL | Title |
| body | TEXT | | Body message |
| data_json | JSON | | Actionable metadata |
| priority | ENUM('LOW','NORMAL','HIGH','URGENT') | NOT NULL, DEFAULT 'NORMAL' | Priority |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

#### `notification_delivery`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Delivery ID |
| notification_id | BIGINT UNSIGNED | FK → notifications(id), NOT NULL, INDEX | Notification |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | Recipient |
| channel | ENUM('IN_APP','EMAIL','PUSH','SMS') | NOT NULL, DEFAULT 'IN_APP' | Delivery channel |
| status | ENUM('PENDING','SENT','DELIVERED','READ','FAILED') | NOT NULL, DEFAULT 'PENDING' | Delivery status |
| sent_at | TIMESTAMP | | Sent timestamp |
| read_at | TIMESTAMP | | Read timestamp |
| error_message | VARCHAR(500) | | Failure reason |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

**Indexes:** (user_id, status, created_at DESC) — fetch unread notifications quickly

### 3.8 Domain: User Statistics & Analytics

#### `user_statistics`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Stat ID |
| user_id | BIGINT UNSIGNED | FK → users(id), UNIQUE, NOT NULL | User |
| total_quizzes_taken | INT | NOT NULL, DEFAULT 0 | Total attempts |
| total_quizzes_passed | INT | NOT NULL, DEFAULT 0 | Passed count |
| total_questions_answered | INT | NOT NULL, DEFAULT 0 | Questions answered |
| total_correct_answers | INT | NOT NULL, DEFAULT 0 | Correct answers |
| total_score | BIGINT | NOT NULL, DEFAULT 0 | Cumulative score |
| total_xp | BIGINT | NOT NULL, DEFAULT 0 | Experience points |
| current_streak | INT | NOT NULL, DEFAULT 0 | Consecutive days |
| longest_streak | INT | NOT NULL, DEFAULT 0 | Best streak |
| total_contests_participated | INT | NOT NULL, DEFAULT 0 | Contest count |
| total_contests_won | INT | NOT NULL, DEFAULT 0 | Wins |
| average_score_pct | DECIMAL(5,2) | | Average performance |
| average_time_per_question_sec | DECIMAL(10,2) | | Speed metric |
| rank_global | INT | NULL | Global rank |
| rank_monthly | INT | NULL | Monthly rank |
| badges_count | INT | NOT NULL, DEFAULT 0 | Badge count |
| achievements_count | INT | NOT NULL, DEFAULT 0 | Achievement count |
| last_active_at | TIMESTAMP | | Last activity |
| last_quiz_at | TIMESTAMP | | Last quiz attempt |
| created_at | TIMESTAMP | NOT NULL | Created |
| updated_at | TIMESTAMP | NOT NULL | Updated |

### 3.9 Domain: File Management

#### `files`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | File ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Public identifier |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | Uploader |
| original_name | VARCHAR(500) | NOT NULL | Original filename |
| stored_name | VARCHAR(500) | NOT NULL | Storage key (UUID-based) |
| mime_type | VARCHAR(100) | NOT NULL | MIME type |
| file_size | BIGINT | NOT NULL | Bytes |
| storage_provider | ENUM('LOCAL') | NOT NULL, DEFAULT 'LOCAL' | Currently local only |
| storage_path | VARCHAR(1000) | NOT NULL | Relative path under `uploads/` |
| file_type | ENUM('PROFILE_PICTURE','BADGE_ICON','QUESTION_IMAGE','CATEGORY_ICON','GENERAL') | NOT NULL | Usage type |
| checksum_md5 | VARCHAR(32) | | Integrity check |
| is_public | BOOLEAN | NOT NULL, DEFAULT FALSE | Public access |
| created_at | TIMESTAMP | NOT NULL | Created timestamp |

> **Note:** `storage_provider` uses single-value ENUM for now. Add `S3`, `MINIO`, `GCS` when cloud migration happens.

### 3.10 Domain: Audit & Compliance

#### `audit_logs`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Log ID |
| uuid | VARCHAR(36) | UNIQUE, NOT NULL | Trace ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NULL, INDEX | Actor |
| username | VARCHAR(50) | | Denormalized username |
| action | VARCHAR(100) | NOT NULL, INDEX | e.g. `USER_LOGIN`, `QUIZ_CREATED` |
| resource_type | VARCHAR(50) | NOT NULL, INDEX | e.g. `quiz`, `user` |
| resource_id | VARCHAR(50) | NULL | ID of affected resource |
| details_json | JSON | | Full context payload |
| ip_address | VARCHAR(45) | NOT NULL | Client IP |
| user_agent | VARCHAR(500) | | Browser user-agent |
| request_id | VARCHAR(36) | | Correlation ID |
| http_method | VARCHAR(10) | | HTTP method |
| http_path | VARCHAR(500) | | Request path |
| http_status | INT | | Response status |
| execution_time_ms | INT | | Request duration |
| is_error | BOOLEAN | NOT NULL, DEFAULT FALSE | Error flag |
| error_message | TEXT | | Error details |
| created_at | TIMESTAMP | NOT NULL, INDEX | Created timestamp |

**Indexes:** (created_at DESC), (user_id, created_at DESC), (action, created_at DESC), (resource_type, resource_id)

### 3.11 Domain: WebSocket Sessions

#### `websocket_sessions`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Session ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | User |
| session_id | VARCHAR(100) | UNIQUE, NOT NULL, INDEX | STOMP session ID |
| connected_at | TIMESTAMP | NOT NULL | Connection time |
| disconnected_at | TIMESTAMP | NULL | Disconnect time |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active flag |
| user_agent | VARCHAR(500) | | Client info |
| ip_address | VARCHAR(45) | | Client IP |

### 3.12 Future: AI Readiness Tables

#### `ai_quiz_generations`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Gen ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | Requester |
| prompt | TEXT | NOT NULL | AI prompt |
| category_id | BIGINT UNSIGNED | FK → categories(id) | Target category |
| difficulty | VARCHAR(20) | | Requested difficulty |
| num_questions | INT | NOT NULL | Requested count |
| generated_json | JSON | | AI response (questions) |
| status | ENUM('PENDING','COMPLETED','FAILED','REVIEWED') | NOT NULL, DEFAULT 'PENDING' | Generation status |
| accepted | BOOLEAN | | Human review decision |
| feedback | TEXT | | User feedback |
| model_version | VARCHAR(50) | | AI model used |
| tokens_used | INT | | Token consumption |
| created_at | TIMESTAMP | NOT NULL | Created |

#### `ai_content_moderations`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Mod ID |
| content_type | VARCHAR(50) | NOT NULL, INDEX | e.g. `question`, `comment` |
| content_id | BIGINT UNSIGNED | NOT NULL | ID of content |
| content_text | TEXT | NOT NULL | Text being moderated |
| ai_decision | VARCHAR(50) | | e.g. `APPROVED`, `FLAGGED`, `REJECTED` |
| confidence_score | DECIMAL(5,4) | | AI confidence |
| flagged_categories | JSON | | Categories flagged |
| reviewed_by | BIGINT UNSIGNED | FK → users(id) | Human reviewer |
| reviewed_at | TIMESTAMP | | Review timestamp |
| final_decision | VARCHAR(50) | | Final decision |
| created_at | TIMESTAMP | NOT NULL | Created |

#### `ai_recommendations`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Rec ID |
| user_id | BIGINT UNSIGNED | FK → users(id), NOT NULL, INDEX | Target user |
| recommendation_type | ENUM('QUIZ','CATEGORY','STUDY_PATH','CONTEST') | NOT NULL | Type |
| recommended_id | BIGINT UNSIGNED | NOT NULL | ID of recommended entity |
| score | DECIMAL(10,4) | | Relevance score |
| reason | VARCHAR(500) | | Explanation |
| is_clicked | BOOLEAN | DEFAULT FALSE | User interaction |
| is_dismissed | BOOLEAN | DEFAULT FALSE | Dismissed |
| created_at | TIMESTAMP | NOT NULL | Created |

---

## 4. ER Diagram Description

```
┌───────────────┐     ┌───────────────────┐     ┌───────────────────┐
│     users     │1──N│  user_roles       │N──M│     roles         │
│               │     │                   │     │                   │
│ PK: id        │     │ PK: user_id       │     │ PK: id            │
│ UQ: username  │     │ PK: role_id       │     │ UQ: name          │
│ UQ: email     │     └───────────────────┘     └────────┬──────────┘
│ UQ: uuid      │                                        │
└───────┬───────┘                                        │
        │                                                1
        │                                                │
        │1                                               │N
        │                                                │
        │               ┌────────────────────┐          │
        │               │  user_permissions   │         │
        │               │                    │          │
        │               │ PK: user_id        │          │
        │               │ PK: permission_id  │          │
        │               └────────┬───────────┘          │
        │                        │                      │
        │                        │N                     │N
        │                        │                      │
        │               ┌────────┴───────────┐  ┌───────┴──────────┐
        │               │   permissions      │  │ role_permissions  │
        │               │                    │  │                  │
        │               │ PK: id             │  │ PK: role_id      │
        │               │ UQ: name           │  │ PK: permission_id│
        │               └────────────────────┘  └──────────────────┘
        │
        │1                ┌─────────────────┐
        ├─────────────────│ refresh_tokens │
        │                 │                 │
        │                 │ PK: id          │
        │                 │ UQ: token_hash  │
        │                 │ FK: user_id     │
        │                 └─────────────────┘
        │
        │1                ┌─────────────────┐
        ├─────────────────│ user_statistics │
        │                 │                 │
        │                 │ PK: id          │
        │                 │ UQ: user_id     │
        │                 └─────────────────┘
        │
        │1                ┌─────────────────┐
        ├─────────────────│ user_badges     │
        │                 │                 │
        │                 │ PK: id          │
        │                 │ FK: user_id     │
        │                 │ FK: badge_id    │
        │                 └────────┬────────┘
        │                         │N
        │1               ┌────────┴────────┐
        ├────────────────│ badges          │
        │                │                 │
        │                │ PK: id          │
        │                │ UQ: name        │
        │                └─────────────────┘
        │
        │1               ┌──────────────────┐
        ├────────────────│ user_achievements │
        │                │                  │
        │                │ PK: id           │
        │                │ FK: user_id      │
        │                │ FK: achievement_id│
        │                └────────┬─────────┘
        │                        │
        │1              ┌────────┴──────────┐
        ├───────────────│ achievements      │
        │               │                  │
        │               │ PK: id           │
        │               │ UQ: name         │
        │               └──────────────────┘
        │
        │1               ┌─────────────────┐
        ├────────────────│ rewards          │
        │                │                  │
        │                │ PK: id           │
        │                │ FK: user_id      │
        │                └─────────────────┘
        │
        │1               ┌─────────────────────┐
        ├────────────────│ notification_delivery│
        │                │                     │
        │                │ PK: id              │
        │                │ FK: user_id         │
        │                │ FK: notification_id │
        │                └─────────┬───────────┘
        │                          │
        │1                ┌────────┴──────────┐
        ├─────────────────│ notifications      │
        │                 │                    │
        │                 │ PK: id             │
        │                 │ UQ: uuid           │
        │                 └────────────────────┘
        │
        │1               ┌─────────────────┐
        ├────────────────│ quiz_attempts   │
        │                │                 │
        │                │ PK: id          │
        │                │ FK: user_id     │
        │                │ FK: quiz_id     │
        │                │ FK: contest_id  │
        │                └────────┬────────┘
        │                         │
        │1               ┌────────┴────────┐
        ├────────────────│ quiz_answers   │
        │                │                │
        │                │ PK: id         │
        │                │ FK: attempt_id │
        │                │ FK: question_id│
        │                └────────────────┘
        │
        │1               ┌────────────────────┐
        ├────────────────│ contest_participants│
        │                │                    │
        │                │ PK: id             │
        │                │ FK: user_id        │
        │                │ FK: contest_id     │
        │                └─────────┬──────────┘
        │                          │
        │1               ┌─────────┴──────────┐
        ├────────────────│ contests           │
        │                │                    │
        │                │ PK: id             │
        │                │ FK: category_id    │
        │                └────────────────────┘
        │
        │1               ┌─────────────────────┐
        ├────────────────│ leaderboard_entries │
        │                │                     │
        │                │ PK: id              │
        │                │ FK: leaderboard_id  │
        │                │ FK: user_id         │
        │                └──────────┬──────────┘
        │                           │
        │1                ┌─────────┴─────────┐
        ├─────────────────│ leaderboards      │
        │                 │                   │
        │                 │ PK: id            │
        │                 │ FK: category_id   │
        │                 └───────────────────┘
        │
        │1               ┌─────────────────┐
        ├────────────────│ files           │
        │                │                 │
        │                │ PK: id          │
        │                │ FK: user_id     │
        │                └─────────────────┘
        │
        │1               ┌──────────────────┐
        ├────────────────│ audit_logs      │
        │                │                  │
        │                │ PK: id           │
        │                │ FK: user_id (opt)│
        │                └──────────────────┘
        │
        │1               ┌────────────────────┐
        ├────────────────│ websocket_sessions │
        │                │                    │
        │                │ PK: id             │
        │                │ FK: user_id        │
        │                └────────────────────┘
        │
        │1               ┌─────────────────────┐
        ├────────────────│ ai_quiz_generations │
        ├────────────────│ ai_content_moderations  │
        ├────────────────│ ai_recommendations  │
        │                │                     │
        │                │ FK: user_id         │
        │                └─────────────────────┘
        │
        │
┌───────┴───────┐     ┌───────────────────┐
│  categories   │1──N│   questions       │
│               │     │                   │
│ PK: id        │     │ PK: id            │
│ UQ: name      │     │ FK: category_id   │
│ FK: parent_id │     │ FK: created_by    │
└───────┬───────┘     └────────┬──────────┘
        │                      │
        │1                     │1
        │                      │
        │               ┌──────┴──────┐
        │               │ question_options│
        │               │             │
        │               │ PK: id      │
        │               │ FK: question_id│
        │               └─────────────┘
        │
        │1              ┌─────────────────┐
        ├───────────────│ quizzes         │
        │               │                 │
        │               │ PK: id          │
        │               │ FK: category_id │
        │               │ FK: created_by  │
        │               └────────┬────────┘
        │                        │
        │1              ┌────────┴────────┐
        │               │ quiz_questions  │
        │               │                 │
        │               │ PK: id          │
        │               │ FK: quiz_id     │
        │               │ FK: question_id │
        │               └─────────────────┘
```

### Entity Relationship Summary

| Relationship | Type | Description |
|---|---|---|
| users ↔ roles | M:N via `user_roles` | Users can have multiple roles |
| roles ↔ permissions | M:N via `role_permissions` | Roles bundle permissions |
| users ↔ permissions | M:N via `user_permissions` | Direct permission grants/denials |
| users ↔ refresh_tokens | 1:N | One user, many refresh tokens |
| users ↔ user_statistics | 1:1 | One stats record per user |
| users ↔ user_badges | 1:N | Many badges per user |
| users ↔ user_achievements | 1:N | Many achievements per user |
| users ↔ rewards | 1:N | Many rewards per user |
| users ↔ quiz_attempts | 1:N | Many quiz attempts per user |
| users ↔ contest_participants | 1:N | Many contest participations |
| users ↔ notification_delivery | 1:N | Many notification deliveries |
| users ↔ files | 1:N | Many uploaded files |
| users ↔ audit_logs | 1:N | Many audit entries |
| users ↔ websocket_sessions | 1:N | Many WebSocket sessions |
| categories ↔ questions | 1:N | One category, many questions |
| questions ↔ question_options | 1:N | One question, many options |
| categories ↔ quizzes | 1:N | One category, many quizzes |
| quizzes ↔ questions | M:N via `quiz_questions` | Quiz-question mapping |
| quizzes ↔ quiz_attempts | 1:N | Many attempts per quiz |
| quiz_attempts ↔ quiz_answers | 1:N | Many answers per attempt |
| contests ↔ contest_participants | 1:N | Many participants per contest |
| contests ↔ quiz_attempts | 1:N | Many attempts in contest context |
| leaderboards ↔ leaderboard_entries | 1:N | Many entries per leaderboard |

---

## 5. Package Structure

```
com.sourashis.quizapp
│
├── QuizappApplication.java
│
├── modules/
│   ├── auth/
│   │   ├── application/
│   │   │   ├── dto/
│   │   │   │   ├── request/LoginRequest.java
│   │   │   │   ├── request/RegisterRequest.java
│   │   │   │   ├── request/RefreshTokenRequest.java
│   │   │   │   └── response/AuthResponse.java
│   │   │   ├── mapper/AuthMapper.java
│   │   │   ├── command/LoginCommand.java
│   │   │   ├── command/RegisterCommand.java
│   │   │   ├── command/RefreshTokenCommand.java
│   │   │   ├── query/CurrentUserQuery.java
│   │   │   └── service/AuthApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/User.java
│   │   │   ├── model/RefreshToken.java
│   │   │   ├── valueobject/Email.java
│   │   │   ├── valueobject/Password.java
│   │   │   ├── valueobject/Username.java
│   │   │   ├── event/UserRegisteredEvent.java
│   │   │   ├── event/UserLoggedInEvent.java
│   │   │   ├── service/PasswordValidationService.java
│   │   │   └── repository/UserRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/UserEntity.java (JPA)
│   │   │   │   ├── entity/RefreshTokenEntity.java (JPA)
│   │   │   │   ├── mapper/UserEntityMapper.java
│   │   │   │   └── repository/JpaUserRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── TokenBlacklistService.java
│   │   │   └── websocket/UserWebSocketHandler.java
│   │   └── interfaces/
│   │       ├── rest/AuthController.java
│   │       ├── rest/UserProfileController.java
│   │       └── graphql/UserResolver.java
│   │
│   ├── role/
│   │   ├── application/
│   │   │   ├── dto/request/RoleRequest.java
│   │   │   ├── dto/response/RoleResponse.java
│   │   │   ├── mapper/RoleMapper.java
│   │   │   ├── command/CreateRoleCommand.java
│   │   │   ├── command/UpdateRoleCommand.java
│   │   │   ├── command/AssignRoleCommand.java
│   │   │   └── service/RoleApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/Role.java
│   │   │   ├── model/Permission.java
│   │   │   ├── valueobject/RoleName.java
│   │   │   ├── valueobject/PermissionName.java
│   │   │   ├── service/RbacValidationService.java
│   │   │   └── repository/RoleRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/RoleEntity.java (JPA)
│   │   │   │   ├── entity/PermissionEntity.java (JPA)
│   │   │   │   ├── mapper/RoleEntityMapper.java
│   │   │   │   └── repository/JpaRoleRepository.java
│   │   │   └── cache/RoleCacheService.java
│   │   └── interfaces/
│   │       └── rest/RoleController.java
│   │
│   ├── quiz/
│   │   ├── application/
│   │   │   ├── dto/request/CreateQuizRequest.java
│   │   │   ├── dto/request/SubmitAnswersRequest.java
│   │   │   ├── dto/response/QuizResponse.java
│   │   │   ├── dto/response/QuizResultResponse.java
│   │   │   ├── command/CreateQuizCommand.java
│   │   │   ├── command/SubmitQuizCommand.java
│   │   │   ├── query/GetQuizQuery.java
│   │   │   ├── query/SearchQuestionsQuery.java
│   │   │   └── service/QuizApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/Quiz.java
│   │   │   ├── model/Question.java
│   │   │   ├── model/QuestionOption.java
│   │   │   ├── model/QuizAttempt.java
│   │   │   ├── model/QuizAnswer.java
│   │   │   ├── model/Category.java
│   │   │   ├── valueobject/Difficulty.java (enum)
│   │   │   ├── valueobject/QuestionType.java (enum)
│   │   │   ├── valueobject/Score.java
│   │   │   ├── event/QuizCompletedEvent.java
│   │   │   ├── event/QuestionReportedEvent.java
│   │   │   ├── service/ScoringService.java
│   │   │   ├── service/QuestionSelectorService.java
│   │   │   └── repository/QuizRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/QuizEntity.java (JPA)
│   │   │   │   ├── entity/QuestionEntity.java (JPA)
│   │   │   │   ├── entity/QuizAttemptEntity.java (JPA)
│   │   │   │   ├── mapper/QuizEntityMapper.java
│   │   │   │   └── repository/JpaQuizRepository.java
│   │   │   ├── search/QuestionSearchService.java (MySQL FULLTEXT; Elasticsearch in future)
│   │   │   ├── cache/QuizCacheService.java
│   │   │   └── event/QuizEventPublisher.java
│   │   └── interfaces/
│   │       ├── rest/QuizController.java
│   │       ├── rest/QuestionController.java
│   │       ├── rest/CategoryController.java
│   │       └── rest/QuizAttemptController.java
│   │
│   ├── contest/
│   │   ├── application/
│   │   │   ├── dto/request/CreateContestRequest.java
│   │   │   ├── dto/response/ContestResponse.java
│   │   │   ├── dto/response/ContestLeaderboardResponse.java
│   │   │   ├── command/JoinContestCommand.java
│   │   │   ├── command/StartContestCommand.java
│   │   │   ├── command/EndContestCommand.java
│   │   │   ├── query/ActiveContestsQuery.java
│   │   │   └── service/ContestApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/Contest.java
│   │   │   ├── model/ContestParticipant.java
│   │   │   ├── valueobject/ContestType.java (enum)
│   │   │   ├── valueobject/ContestStatus.java (enum)
│   │   │   ├── event/ContestStartedEvent.java
│   │   │   ├── event/ContestEndedEvent.java
│   │   │   ├── event/PlayerJoinedContestEvent.java
│   │   │   ├── service/ContestSchedulerService.java
│   │   │   └── repository/ContestRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/ContestEntity.java
│   │   │   │   ├── entity/ContestParticipantEntity.java
│   │   │   │   ├── mapper/ContestEntityMapper.java
│   │   │   │   └── repository/JpaContestRepository.java
│   │   │   ├── schedule/ContestScheduler.java (Spring @Scheduled)
│   │   │   └── websocket/ContestWebSocketHandler.java
│   │   └── interfaces/
│   │       └── rest/ContestController.java
│   │
│   ├── leaderboard/
│   │   ├── application/
│   │   │   ├── dto/response/LeaderboardEntryResponse.java
│   │   │   ├── command/RecalculateLeaderboardCommand.java
│   │   │   ├── query/GetLeaderboardQuery.java
│   │   │   ├── query/GetUserRankQuery.java
│   │   │   └── service/LeaderboardApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/Leaderboard.java
│   │   │   ├── model/LeaderboardEntry.java
│   │   │   ├── valueobject/LeaderboardType.java (enum)
│   │   │   ├── event/LeaderboardUpdatedEvent.java
│   │   │   ├── service/RankingService.java
│   │   │   └── repository/LeaderboardRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/LeaderboardEntity.java
│   │   │   │   └── repository/JpaLeaderboardRepository.java
│   │   │   ├── cache/LeaderboardCacheService.java (Redis Sorted Sets)
│   │   │   └── websocket/LeaderboardWebSocketHandler.java
│   │   └── interfaces/
│   │       └── rest/LeaderboardController.java
│   │
│   ├── notification/
│   │   ├── application/
│   │   │   ├── dto/request/SendNotificationRequest.java
│   │   │   ├── dto/response/NotificationResponse.java
│   │   │   ├── command/SendNotificationCommand.java
│   │   │   ├── command/MarkAsReadCommand.java
│   │   │   ├── query/GetUserNotificationsQuery.java
│   │   │   └── service/NotificationApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/Notification.java
│   │   │   ├── model/NotificationDelivery.java
│   │   │   ├── valueobject/NotificationType.java (enum)
│   │   │   ├── valueobject/NotificationChannel.java (enum)
│   │   │   ├── event/NotificationCreatedEvent.java
│   │   │   └── repository/NotificationRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/NotificationEntity.java
│   │   │   │   └── repository/JpaNotificationRepository.java
│   │   │   ├── websocket/NotificationWebSocketHandler.java
│   │   │   ├── push/                          [Future]
│   │   │   │   ├── PushNotificationService.java
│   │   │   │   └── FcmClient.java
│   │   │   └── email/                         [Future]
│   │   │       └── EmailService.java
│   │   └── interfaces/
│   │       └── rest/NotificationController.java
│   │
│   ├── file/
│   │   ├── application/
│   │   │   ├── dto/response/FileUploadResponse.java
│   │   │   ├── command/UploadFileCommand.java
│   │   │   ├── command/DeleteFileCommand.java
│   │   │   └── service/FileApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/File.java
│   │   │   ├── valueobject/StorageProvider.java (enum)
│   │   │   ├── valueobject/FileType.java (enum)
│   │   │   └── repository/FileRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/FileEntity.java
│   │   │   │   └── repository/JpaFileRepository.java
│   │   │   └── storage/
│   │   │       ├── FileStorageService.java (interface)
│   │   │       └── LocalFileStorageService.java   (Phase 0)
│   │   │       └── [S3FileStorageService.java — Future]
│   │   │       └── [MinioFileStorageService.java — Future]
│   │   └── interfaces/
│   │       └── rest/FileController.java
│   │
│   ├── analytics/
│   │   ├── application/
│   │   │   ├── dto/response/UserStatisticsResponse.java
│   │   │   ├── dto/response/AdminDashboardResponse.java
│   │   │   ├── query/GetUserStatsQuery.java
│   │   │   ├── query/GetAdminDashboardQuery.java
│   │   │   └── service/AnalyticsApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/UserStatistics.java
│   │   │   └── repository/UserStatisticsRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/UserStatisticsEntity.java
│   │   │   │   └── repository/JpaUserStatisticsRepository.java
│   │   │   └── event/StatisticsEventSubscriber.java
│   │   └── interfaces/
│   │       └── rest/AnalyticsController.java
│   │
│   ├── reward/
│   │   ├── application/
│   │   │   ├── command/AwardBadgeCommand.java
│   │   │   ├── command/CheckAchievementsCommand.java
│   │   │   └── service/RewardApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/Badge.java
│   │   │   ├── model/Achievement.java
│   │   │   ├── model/Reward.java
│   │   │   ├── model/UserBadge.java
│   │   │   ├── model/UserAchievement.java
│   │   │   ├── service/BadgeEvaluatorService.java
│   │   │   ├── service/AchievementEvaluatorService.java
│   │   │   ├── service/XpService.java
│   │   │   └── repository/RewardRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/BadgeEntity.java
│   │   │   │   ├── entity/AchievementEntity.java
│   │   │   │   └── repository/JpaRewardRepository.java
│   │   │   └── event/RewardEventSubscriber.java
│   │   └── interfaces/
│   │       └── rest/RewardController.java
│   │
│   ├── audit/
│   │   ├── application/
│   │   │   ├── query/GetAuditLogsQuery.java
│   │   │   └── service/AuditApplicationService.java
│   │   ├── domain/
│   │   │   ├── model/AuditLog.java
│   │   │   └── repository/AuditRepository.java (interface)
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/AuditLogEntity.java
│   │   │   │   └── repository/JpaAuditRepository.java
│   │   │   ├── aop/AuditAspect.java
│   │   │   └── async/AsyncAuditLogger.java
│   │   └── interfaces/
│   │       └── rest/AuditController.java
│   │
│   └── ai/                               (FUTURE: Scaffold Only)
│       ├── application/
│       │   ├── command/AiGenerateQuizCommand.java
│       │   ├── command/AiModerateContentCommand.java
│       │   └── service/AiApplicationService.java
│       ├── domain/
│       │   ├── model/AiQuizGeneration.java
│       │   ├── model/AiContentModeration.java
│       │   ├── model/AiRecommendation.java
│       │   └── repository/AiRepository.java (interface)
│       ├── infrastructure/
│       │   ├── persistence/
│       │   │   ├── entity/AiQuizGenerationEntity.java
│       │   │   ├── entity/AiContentModerationEntity.java
│       │   │   └── repository/JpaAiRepository.java
│       │   ├── client/
│       │   │   ├── OpenAiClient.java
│       │   │   └── AiModelRouter.java  (vendor abstraction)
│       │   └── pipeline/
│       │       ├── QuizGenerationPipeline.java
│       │       └── ModerationPipeline.java
│       └── interfaces/
│           └── rest/AiController.java
│
├── shared/
│   ├── common/
│   │   ├── annotation/CurrentUser.java
│   │   ├── annotation/RateLimited.java
│   │   ├── util/
│   │   │   ├── UuidGenerator.java
│   │   │   └── DateTimeUtil.java
│   │   └── constant/
│   │       ├── AppConstants.java
│   │       └── PermissionConstants.java
│   ├── exception/
│   │   ├── BaseException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── DuplicateResourceException.java
│   │   ├── UnauthorizedException.java
│   │   ├── ForbiddenException.java
│   │   ├── BusinessRuleException.java
│   │   └── GlobalExceptionHandler.java  (@ControllerAdvice)
│   ├── response/
│   │   ├── ApiResponse.java
│   │   └── PageResponse.java
│   ├── security/
│   │   ├── CurrentUserResolver.java (HandlerMethodArgumentResolver)
│   │   ├── PermissionEvaluator.java
│   │   └── RateLimitAspect.java
│   ├── event/
│   │   ├── DomainEvent.java (abstract)
│   │   ├── EventPublisher.java
│   │   └── EventSubscriber.java (interface)
│   └── validation/
│       ├── ValidPassword.java
│       ├── ValidEmail.java
│       └── UniqueUsernameValidator.java
│
├── infrastructure/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── WebSocketConfig.java
│   │   ├── RedisConfig.java
│   │   ├── [ElasticsearchConfig.java -- Future]
│   │   ├── AsyncConfig.java
│   │   ├── SchedulingConfig.java
│   │   ├── CorsConfig.java
│   │   ├── SwaggerConfig.java (OpenAPI 3.0)
│   │   ├── FlywayConfig.java
│   │   └── ObjectMapperConfig.java
│   ├── data/
│   │   ├── initializer/DataInitializer.java
│   │   └── migration/ (Flyway SQL scripts)
│   └── monitoring/
│       ├── [MetricsConfig.java -- Future (Micrometer + Prometheus)]
│       ├── HealthIndicator.java
│       └── LoggingAspect.java
```

### Package Justification

| Package | Purpose |
|---------|---------|
| `modules/auth` | Authentication, user registration, login, token management, user profile |
| `modules/role` | RBAC — role + permission CRUD, dynamic assignment |
| `modules/quiz` | Core quiz features: question management, quiz creation, answering, scoring |
| `modules/contest` | Time-bound contests: daily/weekly/monthly, participant management |
| `modules/leaderboard` | Ranking system: global, category, contest-specific leaderboards |
| `modules/notification` | Multi-channel notifications: in-app, email, push |
| `modules/file` | File upload/download, local filesystem storage (Phase 0); S3/MinIO abstraction ready for future |
| `modules/analytics` | User statistics, admin dashboards, reporting |
| `modules/reward` | Badges, achievements, XP, reward system |
| `modules/audit` | Audit logging, compliance tracking, AOP-based logging |
| `modules/ai` | Future AI integration scaffold (quiz generation, moderation, recommendations) |
| `shared/common` | Cross-cutting annotations, utilities, constants |
| `shared/exception` | Centralized exception hierarchy and global handler |
| `shared/response` | Standardized API response wrapper |
| `shared/security` | Security utilities, permission evaluator, rate limiting |
| `shared/event` | Domain event infrastructure (abstract classes, publisher, subscriber) |
| `shared/validation` | Custom validation annotations |
| `infrastructure/config` | All Spring configuration classes |
| `infrastructure/data` | Data initialization and database migrations |
| `infrastructure/monitoring` | Observability: metrics, health checks, logging |

### Package Principles

1. **Domain Isolation:** Each module is a self-contained bounded context with its own domain, application, infrastructure, and interfaces layers
2. **No Circular Dependencies:** Modules depend on `shared` only. Cross-module communication via domain events
3. **Package by Feature, Not Layer:** Within modules, layered structure exists. At top level, grouped by feature
4. **Infrastructure Tucked Away:** Implementations behind interfaces; domain never imports JPA, Redis, or HTTP concerns
5. **Future-Proof:** The `ai` module is scaffolded with the same layered structure, ready for implementation

---

## 6. Service Layer Design

### 6.1 Application Services (Orchestration)

| Service | Responsibilities |
|---------|-----------------|
| `AuthApplicationService` | Coordinates registration, login, token refresh, logout. Delegates to domain services and infrastructure |
| `UserApplicationService` | Profile CRUD, account management, search |
| `RoleApplicationService` | Role/permission CRUD, assignment, validation |
| `QuizApplicationService` | Quiz creation, answer submission, validation orchestration |
| `ContestApplicationService` | Contest lifecycle (create, start, end), participant management |
| `LeaderboardApplicationService` | Leaderboard queries, rank calculation requests |
| `NotificationApplicationService` | Send notifications, mark as read, query user notifications |
| `FileApplicationService` | Upload/download orchestration, storage routing |
| `AnalyticsApplicationService` | Dashboard aggregation, user statistics queries |
| `RewardApplicationService` | Badge/achievement evaluation, XP calculation |

### 6.2 Domain Services (Business Logic)

| Service | Responsibilities |
|---------|-----------------|
| `PasswordValidationService` | Password strength rules (min length, complexity) |
| `RbacValidationService` | Permission inheritance, conflict resolution, role hierarchy validation |
| `ScoringService` | Score calculation, time bonus, streak bonus |
| `QuestionSelectorService` | Random selection algorithm, difficulty balancing |
| `ContestSchedulerService` | Auto-create recurring contests, cleanup expired contests |
| `RankingService` | Rank calculation, tiebreaker strategies (score → time → earliest finish) |
| `BadgeEvaluatorService` | Check badge criteria on domain events |
| `AchievementEvaluatorService` | Progress tracking and completion evaluation |
| `XpService` | XP gain calculation and level progression |

### 6.3 Service Interaction Pattern

```
┌───────────────────┐
│  REST Controller   │
└─────────┬─────────┘
          │ invokes
┌─────────▼─────────┐
│ ApplicationService  │ (Orchestrator — no business logic)
│  • Validates input
│  • Calls domain service(s)
│  • Persists via repositories
│  • Publishes events
└─────────┬─────────┘
          │
     ┌────┴────┐
     │         │
┌────▼────┐ ┌──▼──────────┐
│ Domain   │ │ Repository  │
│ Service  │ │ (Interface) │
│(Business │ │             │
│ Logic)   │ │             │
└─────────┘ └──────┬──────┘
                    │
           ┌────────▼─────────┐
           │ Infrastructure    │
           │ (JPA/Redis/ES)    │
           └──────────────────┘
```

---

## 7. Repository Layer Design

### 7.1 Interface Definitions (Domain Layer)

```java
// Inside each module's domain/repository/ package

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    User save(User user);
    void delete(User user);
    Page<User> search(String query, Pageable pageable);
}

public interface RoleRepository {
    Optional<Role> findById(RoleId id);
    Optional<Role> findByName(RoleName name);
    Set<Role> findAll();
    boolean existsByName(RoleName name);
    Role save(Role role);
    void delete(Role role);
}

public interface QuizRepository {
    Optional<Quiz> findById(QuizId id);
    Page<Quiz> findByCategory(CategoryId categoryId, Pageable pageable);
    Page<Quiz> search(String query, Pageable pageable);
    Quiz save(Quiz quiz);
    void delete(Quiz quiz);
}

public interface QuestionRepository {
    Optional<Question> findById(QuestionId id);
    Page<Question> findByCategory(CategoryId categoryId, Pageable pageable);
    List<Question> findRandomByCategory(CategoryId categoryId, int limit, Difficulty difficulty);
    Page<Question> search(String query, Pageable pageable);
    Question save(Question question);
    void delete(Question question);
}
```

### 7.2 Implementation Strategy

| Concern | Implementation |
|---------|---------------|
| Primary Persistence | JPA/Hibernate with MySQL 8 |
| Complex Queries | Spring Data JPA Specifications + QueryDSL |
| Search | MySQL FULLTEXT indexes (Phase 0); Elasticsearch for future |
| Leaderboard | Redis Sorted Sets for real-time; MySQL for historical |
| Caching | Redis Cache aside pattern for hot data (roles, permissions, categories) |
| Read Models | Separate JPA entities for read-optimized views (CQRS readiness) |
| Auditing | Spring Data JPA Auditing (`@CreatedDate`, `@LastModifiedDate`) |
| Soft Delete | `is_active` boolean flag; repositories filter `WHERE is_active = TRUE` |

### 7.3 Repository Implementation Pattern

```
┌──────────────────────────────────────────────┐
│ Domain Layer                                  │
│  interface UserRepository {                   │
│    Optional<User> findById(UserId id);        │
│  }                                            │
└─────────────────┬────────────────────────────┘
                  │ implements
┌─────────────────▼────────────────────────────┐
│ Infrastructure Layer                          │
│                                               │
│  interface JpaUserRepository                  │
│    extends JpaRepository<UserEntity, Long> {} │
│                                               │
│  class UserRepositoryImpl implements          │
│        UserRepository {                       │
│                                               │
│    private final JpaUserRepository jpa;       │
│    private final UserEntityMapper mapper;     │
│                                               │
│    public Optional<User> findById(UserId id){ │
│      return jpa.findById(id.value())          │
│        .map(mapper::toDomain);                │
│    }                                          │
│  }                                            │
└──────────────────────────────────────────────┘
```

---

## 8. Security Architecture

### 8.1 Authentication Flow

```
┌──────┐         ┌──────────┐        ┌──────────┐        ┌──────────┐
│Client│         │ Gateway  │        │   App    │        │   DB     │
└──┬───┘         └────┬─────┘        └────┬─────┘        └────┬─────┘
   │                  │                   │                   │
   │  POST /auth/login│                   │                   │
   │─────────────────►│                   │                   │
   │                  │  Forward Request  │                   │
   │                  │─────────────────►│                   │
   │                  │                   │  Authenticate     │
   │                  │                   │─────────────────►│
   │                  │                   │  Verify Creds     │
   │                  │                   │◄─────────────────│
   │                  │                   │                   │
   │                  │                   │ Generate JWT      │
   │                  │                   │ (Access + Refresh)│
   │                  │                   │                   │
   │  Tokens + User   │                   │                   │
   │◄─────────────────│◄──────────────────│                   │
   │                  │                   │                   │
   │                  │                   │                   │
   │  POST /quiz/create (Bearer JWT)      │                   │
   │─────────────────►│                   │                   │
   │                  │ JWT Filter        │                   │
   │                  │ • Validate JWT    │                   │
   │                  │ • Extract claims  │                   │
   │                  │ • Set SecurityContext                │
   │                  │─────────────────►│                   │
   │                  │                   │ @PreAuthorize     │
   │                  │                   │ ("quiz:create")  │
   │                  │                   │  Check Authority  │
   │                  │                   │                   │
   │  Response        │                   │                   │
   │◄─────────────────│◄──────────────────│                   │
   │                  │                   │                   │
```

### 8.2 Token Structure

**Access Token (JWT):**
```json
{
  "sub": "johndoe",
  "userId": 123,
  "roles": ["ROLE_USER", "ROLE_MODERATOR"],
  "permissions": ["question:read", "quiz:read", "quiz:attempt", "category:read", "content:moderate"],
  "iat": 1718000000,
  "exp": 1718003600,
  "jti": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Refresh Token:** Opaque string stored as SHA-256 hash in DB. Longer-lived (7-30 days).

### 8.3 Security Configuration

| Aspect | Implementation |
|--------|---------------|
| Password Encoding | BCrypt with strength 12 |
| Access Token | JWT (HS256), 15-60 min expiry |
| Refresh Token | Opaque, SHA-256 hash in DB, 7-30 day expiry, rotation on use |
| Token Storage | Refresh tokens in DB; access tokens stateless (no server-side storage) |
| JWT Secret | 256-bit key, injected via environment variable, rotated on breach |
| Multi-Factor | Interface for future TOTP/WebAuthn support |
| OAuth2 / SSO | Spring Security OAuth2 Client for Google/GitHub login |
| Session Management | Stateless (no HTTP session) |

### 8.4 RBAC + Dynamic Permissions

```
Authorization Hierarchy:
┌────────────────────────────────────────────┐
│  1. Is the user a SUPER_ADMIN?            │
│     → YES: GRANT ALL (short-circuit)      │
│     → NO: Continue                        │
├────────────────────────────────────────────┤
│  2. Does user_permissions DENY this perm? │
│     → YES: DENY (explicit override)       │
│     → NO: Continue                        │
├────────────────────────────────────────────┤
│  3. Does any of user's roles grant perm?  │
│     → YES: GRANT                          │
│     → NO: Continue                        │
├────────────────────────────────────────────┤
│  4. Does user_permissions GRANT this perm?│
│     → YES: GRANT                          │
│     → NO: DENY                            │
└────────────────────────────────────────────┘
```

### 8.5 Method-Level Security

```java
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @PreAuthorize("hasAuthority('quiz:create')")
    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(...) { }

    @PreAuthorize("hasAuthority('quiz:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuiz(@PathVariable Long id) { }

    @PreAuthorize("hasAuthority('quiz:attempt')")
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<QuizResultResponse>> submitQuiz(...) { }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) { }
}
```

### 8.6 API Security

| Measure | Implementation |
|---------|---------------|
| Rate Limiting | Token Bucket algorithm via Bucket4j + Redis; per-user, per-IP, per-endpoint tiers |
| CORS | Whitelist of allowed origins in config |
| CSRF | Disabled (stateless JWT auth); enabled if cookies used |
| Security Headers | `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Strict-Transport-Security` |
| Input Validation | Jakarta Validation (`@Valid`, `@NotNull`, `@Size`, custom validators) |
| SQL Injection | JPA parameterized queries (no string concatenation) |
| XSS | Output encoding, Content-Security-Policy header |
| Request Size | `spring.servlet.multipart.max-file-size=10MB` |
| IP Whitelisting | Admin endpoints restricted to VPN/internal IPs |

### 8.7 Rate Limiting Strategy

**Phase 0:** In-memory rate limiting (concurrent HashMap with scheduled cleanup).
**Upgrade:** Redis-backed rate limiting via Bucket4j when multiple instances are deployed.

| Tier | Limit | Scope | Endpoints |
|------|-------|-------|-----------|
| Public | 10 req/min | Per IP | `/auth/register`, `/auth/login` |
| Authenticated | 100 req/min | Per user | General API |
| Quiz Attempts | 60 req/min | Per user | `/quizzes/*/submit` |
| Admin | 200 req/min | Per admin | Admin endpoints |
| File Upload | 10 req/min | Per user | `/files/upload` |

### 8.8 Audit Logging

| Action | Logged Data | Retention |
|--------|-------------|-----------|
| User Login | user_id, IP, user-agent, timestamp, success/fail | 90 days |
| Role Changes | admin_id, target_user, old_role, new_role | 1 year |
| Permission Changes | admin_id, role_id, added/removed permissions | 1 year |
| Quiz CRUD | user_id, quiz_id, action (create/update/delete) | 90 days |
| Contest CRUD | user_id, contest_id, action | 90 days |
| File Operations | user_id, file_id, action (upload/download/delete) | 90 days |
| Failed Requests | IP, path, method, headers | 30 days |
| Data Exports | user_id, export_type, timestamp | 1 year |

---

## 9. Microservice Migration Plan

### 9.1 Current State: Modular Monolith

The architecture is designed as a **modular monolith** with strict bounded contexts. Each module:
- Has its own domain model
- Communicates via domain events (not direct service calls)
- Is independently deployable with minimal change
- Shares only the `shared` library

### 9.2 Migration Phases

```
Phase 0 ────► Phase 1 ────► Phase 2 ────► Phase 3 ────► Phase 4
(Current)     (Extract)     (Split DB)    (Eventual    (Full Mesh)
 Modular      Auth + User   Quiz +        Consistency)  Service
 Monolith     Services      Contest       Async         Mesh
```

### Phase 0: Modular Monolith (Now — What We Build)

```
┌──────────────────────────────────────────────────────┐
│              Single JAR (spring-boot-maven-plugin)    │
│                                                      │
│  Auth │ User │ Quiz │ Contest │ Leaderboard          │
│  Notification │ File │ Analytics │ Audit │ AI(scaff)│
│                                                      │
│  ├── MySQL 8 (Single DB: quizapp_db)                 │
│  ├── Redis 7 (Cache + Leaderboards + Rate Limiting)  │
│  ├── Local File System (uploads/)                    │
│  ├── WebSocket (SimpleBroker, single JVM)            │
│  └── Spring Events (@Async) for domain events        │
│                                                      │
│  No paid/cloud services. All self-hosted.            │
└──────────────────────────────────────────────────────┘
```

### Phase 1: Extract Auth + User Services

```diff
- Monolith serves everything
+ ┌──────────┐  ┌──────────┐  ┌─────────────────┐
+ │ Auth     │  │ User     │  │ Monolith (Rest)  │
+ │ Service  │  │ Service  │  │ Quiz, Contest,   │
+ │ :8081    │  │ :8082    │  │ Leaderboard...   │
+ │          │  │          │  │ :8080            │
+ │ DB: auth │  │ DB: users│  │ DB: quizapp     │
+ └──────────┘  └──────────┘  └─────────────────┘
+       │              │               │
+       └──────────────┴───────────────┘
+                    │ HTTP/gRPC
+               ┌────▼────┐
+               │  Redis  │
+               │ (Cache) │
+               └─────────┘
```

**Changes:**
- `Auth` module becomes standalone Spring Boot app with its own `auth_db`
- `User` module becomes standalone with `user_db`
- Remaining modules stay as monolith
- Inter-service communication via REST (Feign clients) + Redis for cache
- Shared JWT secret/key pair for auth verification

### Phase 2: Extract Quiz + Contest Services

```diff
- Auth + User are separate
+ Quiz and Contest extracted
+
+ ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
+ │ Auth     │  │ User     │  │ Quiz     │  │ Contest  │
+ │ Service  │  │ Service  │  │ Service  │  │ Service  │
+ └──────────┘  └──────────┘  └──────────┘  └──────────┘
+       │              │              │             │
+       └──────────────┴──────────────┴─────────────┘
+                        │
+              ┌─────────▼─────────┐
+              │  Message Queue    │
+              │  (RabbitMQ/Kafka) │
+              │  - Domain Events  │
+              │  - Async Commands │
+              └───────────────────┘
+                        │
+        ┌───────────────┼───────────────┐
+        │               │               │
+  ┌─────▼─────┐  ┌──────▼──────┐  ┌────▼─────┐
+  │Notification│  │Leaderboard │  │Analytics │
+  │ Service   │  │  Service   │  │ Service  │
+  └───────────┘  └────────────┘  └──────────┘
```

### Phase 3: Eventual Consistency + CQRS

- All services communicate via async events (Kafka topics)
- Each service has its own read-only replica or event-sourced read model
- API Gateway (Spring Cloud Gateway) handles routing, auth, rate limiting
- Service discovery via Eureka / Consul
- Circuit breakers (Resilience4j) for fault tolerance

### Phase 4: Service Mesh (Future)

- Istio/Linkerd for traffic management
- Distributed tracing (Jaeger)
- Separate read/write services per domain (CQRS)
- Event sourcing for audit-critical domains (quiz attempts, rewards)

### 9.3 Database Split Strategy

| Microservice | Database | Tables |
|-------------|----------|--------|
| Auth Service | `auth_db` | users, refresh_tokens, websocket_sessions |
| User Service | `user_db` | user_profiles, user_settings (subset of users table) |
| Role Service | `role_db` | roles, permissions, role_permissions, user_roles, user_permissions |
| Quiz Service | `quiz_db` | categories, questions, question_options, quizzes, quiz_questions |
| Contest Service | `contest_db` | contests, contest_participants, contest_leaderboard |
| Leaderboard Service | `leaderboard_db` | leaderboards, leaderboard_entries |
| Notification Service | `notification_db` | notifications, notification_delivery |
| File Service | `file_db` | files (storage in S3/MinIO) |
| Analytics Service | `analytics_db` | user_statistics (denormalized from events) |
| Reward Service | `reward_db` | badges, user_badges, achievements, user_achievements, rewards |
| Audit Service | `audit_db` | audit_logs |

### 9.4 Migration Success Criteria

| Metric | Target |
|--------|--------|
| Zero downtime during migration | 99.99% availability |
| Rollback capability per phase | < 30 min rollback |
| Data consistency | Eventual consistency within 5 seconds |
| Latency increase | < 50ms added per inter-service call |
| Test coverage | > 85% before each phase |

---

## 10. Redis Usage Plan

### 10.1 Caching Strategy

| Cache Key Pattern | TTL | Purpose | Invalidation |
|------------------|-----|---------|-------------|
| `role:{roleId}` | 1 hour | Role + permissions data | On role/permission update |
| `role:all` | 30 min | All roles list | On any role/permission change |
| `permission:all` | 1 hour | Permission catalog | On permission create |
| `category:{id}` | 1 hour | Category details | On category update |
| `category:all` | 30 min | All categories | On category CRUD |
| `user:{userId}:profile` | 30 min | User profile | On profile update |
| `quiz:{quizId}` | 1 hour | Quiz with questions | On quiz update |
| `question:{id}` | 2 hours | Single question | On question update |
| `config:app` | 1 day | Application configuration | Manual refresh |

### 10.2 Cross-Module Event Communication (Phase 0)

In Phase 0, domain events are published and consumed **in-process** using Spring's `ApplicationEventPublisher`:

```java
// Event publication (synchronous or async)
@Service
public class QuizEventPublisher {
    @Autowired private ApplicationEventPublisher publisher;

    @Async("quizEventExecutor")
    public void publishQuizCompleted(QuizCompletedEvent event) {
        publisher.publishEvent(event);
    }
}
```

```java
// Event consumption (in same JVM)
@Component
public class LeaderboardEventSubscriber {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onQuizCompleted(QuizCompletedEvent event) {
        leaderboardService.updateScores(event.getUserId(), event.getScore());
    }
}
```

**Thread pool configuration:**
```java
@Bean(name = "quizEventExecutor")
public Executor quizEventExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("quiz-event-");
    executor.setRejectedExecutionHandler(new CallerRunsPolicy());
    return executor;
}
```

> **Future:** Replace `@Async` with message queue (RabbitMQ/Kafka). The event objects remain the same — only the transport changes.

### 10.3 Leaderboard (Redis Sorted Sets)

| Key Pattern | Type | Purpose |
|-------------|------|---------|
| `leaderboard:global` | Sorted Set | All-time global ranking, score as weight |
| `leaderboard:category:{categoryId}` | Sorted Set | Category-specific ranking |
| `leaderboard:weekly:{year}:{week}` | Sorted Set | Weekly contest rankings |
| `leaderboard:monthly:{year}:{month}` | Sorted Set | Monthly contest rankings |
| `leaderboard:contest:{contestId}` | Sorted Set | Live contest leaderboard |
| `leaderboard:user:{userId}:rank` | String | Cached user rank summary |

**Operations:**
- `ZINCRBY leaderboard:global {scoreChange} {userId}` — Update score atomically
- `ZREVRANGE leaderboard:global 0 99 WITHSCORES` — Top 100
- `ZRANK leaderboard:global {userId}` — User's rank
- `ZREVRANK leaderboard:global {userId}` — User's rank (high-to-low)
- `ZSCORE leaderboard:global {userId}` — User's score
- `ZCOUNT leaderboard:global -inf +inf` — Total participants

**Tiebreaker Strategy:**
- Primary sort: Score (higher wins)
- Secondary sort: Time taken (lower wins)
- Implemented via composite score: `score * 10^10 + (max_time - time_taken)`

### 10.3 Session & Rate Limiting

| Key Pattern | Type | Purpose | TTL |
|-------------|------|---------|-----|
| `ratelimit:{userId}:{endpoint}` | String (counter) | Per-user rate limit counter | 1 minute |
| `ratelimit:{ip}:auth` | String (counter) | Login attempt throttling | 1 minute |
| `ratelimit:{ip}:register` | String (counter) | Registration throttling | 1 hour |
| `token:blacklist:{jti}` | String | Logged-out token blacklist | Until token expiry |
| `lock:user:{userId}` | String (lock) | Distributed lock for concurrent operations | 10 seconds |

### 10.4 Real-Time Data

| Key Pattern | Type | Purpose | TTL |
|-------------|------|---------|-----|
| `ws:user:{userId}:session` | Set | Active WebSocket session IDs | Session lifetime |
| `ws:contest:{contestId}:participants` | Set | Active contest participants | Contest duration |
| `notification:unread:{userId}` | Set | Unread notification IDs | Until read |
| `contest:{contestId}:live` | Hash | Live contest state | Contest duration |

### 10.5 Cache-Aside Pattern Implementation

```java
@Service
public class RoleCacheService {

    @Autowired
    private RedisTemplate<String, Role> redisTemplate;

    private static final String KEY_PREFIX = "role:";
    private static final long TTL_HOURS = 1;

    public Optional<Role> findById(Long roleId) {
        String key = KEY_PREFIX + roleId;
        Role cached = redisTemplate.opsForValue().get(key);
        if (cached != null) return Optional.of(cached);

        // Cache miss — load from DB
        Optional<Role> role = roleRepository.findById(roleId);
        role.ifPresent(r -> redisTemplate.opsForValue()
            .set(key, r, TTL_HOURS, TimeUnit.HOURS));
        return role;
    }

    @CacheEvict(value = "roles", key = "#roleId")
    public void evict(Long roleId) {
        redisTemplate.delete(KEY_PREFIX + roleId);
    }

    @CacheEvict(value = "roles", allEntries = true)
    public void evictAll() {
        redisTemplate.delete(KEY_PREFIX + "all");
    }
}
```

### 10.6 Redis Configuration

| Setting | Value | Rationale |
|---------|-------|-----------|
| Max Memory Policy | `allkeys-lru` | Evict least recently used when full |
| Persistence | RDB snapshots every 5 min | Quick recovery; full persistence not needed (source of truth is MySQL) |
| Max Memory | 4 GB (adjustable) | Depends on leaderboard size and cache volume |
| Cluster | Redis Sentinel (3 nodes) | High availability |
| Connection Pool | 50 connections per app instance | Sufficient for typical workloads |

---

## 11. WebSocket Architecture

### 11.1 Technology Stack

- **Protocol:** STOMP over WebSocket (SockJS fallback)
- **Server:** Spring WebSocket (`spring-boot-starter-websocket`)
- **Message Broker:** Simple in-memory broker (`SimpleBroker`) — single JVM only. Upgrade to RabbitMQ STOMP relay when scaling to multiple instances.
- **Client Library:** @stomp/stomp.js (web), StompKit (iOS), okhttp3-ws (Android)

### 11.2 Architecture Diagram (Phase 0: Single Instance)

```
┌──────────┐     ┌──────────┐     ┌──────────┐
│  Client 1 │     │  Client 2 │     │  Client N│
│  Browser  │     │  Mobile   │     │  Browser │
└─────┬────┘     └─────┬────┘     └─────┬────┘
      │                │                │
      │           STOMP over WebSocket   │
      └────────────────┬────────────────┘
                       │
              ┌────────▼────────┐
              │   Spring Boot   │
              │   (Single JVM)  │
              │                 │
              │  ┌───────────┐  │
              │  │ WebSocket │  │
              │  │ Config    │  │
              │  └─────┬─────┘  │
              │        │        │
              │  ┌─────▼─────┐  │
              │  │ Simple    │  │
              │  │ Broker    │  │
              │  │(In-Memory)│  │
              │  └─────┬─────┘  │
              │        │        │
              │  ┌─────▼─────┐  │
              │  │ App Event │  │
              │  │ Publisher │  │
              │  └───────────┘  │
              └─────────────────┘
```

> **Note:** For multi-instance deployment (future), replace SimpleBroker with RabbitMQ STOMP relay. The WebSocketConfig abstraction makes this a one-line change (`enableStompBrokerRelay` vs `enableSimpleBroker`).

### 11.3 Topic/Destination Design

| Destination | Type | Purpose | Auth |
|-------------|------|---------|------|
| `/topic/contest/{contestId}/leaderboard` | Topic | Live leaderboard updates during contest | Subscribe: contest participant |
| `/topic/contest/{contestId}/status` | Topic | Contest start/end/extension events | Subscribe: contest participant |
| `/queue/notifications/{userId}` | Queue | Personal notifications (in-app) | Subscribe: authenticated user |
| `/topic/leaderboard/global` | Topic | Global ranking changes | Subscribe: authenticated user |
| `/topic/leaderboard/{type}/{period}` | Topic | Category/weekly/monthly updates | Subscribe: authenticated user |
| `/topic/admin/dashboard` | Topic | Admin dashboard live updates | Subscribe: ADMIN/SUPER_ADMIN |
| `/topic/system/announcements` | Topic | Broadcast announcements | Subscribe: all authenticated |
| `/user/queue/errors` | Queue | Per-user error messages | Subscribe: authenticated user |

### 11.4 Event Flow: Contest Leaderboard Update

```
1. User submits quiz answer
2. QuizApplicationService processes submission
3. Score calculated → QuizCompletedEvent published
4. ContestEventSubscriber receives event
5. LeaderboardApplicationService.updateContestScore(contestId, userId, score)
6. Redis ZINCRBY leaderboard:contest:{contestId} {score} {userId}
7. Redis ZREVRANGE leaderboard:contest:{contestId} 0 49 → top 50
8. WebSocket message sent to /topic/contest/{contestId}/leaderboard
9. (Future: RabbitMQ fanout exchange distributes to all instances)
10. All connected contest participants receive update
```

### 11.5 WebSocket Security

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    // Validate JWT token
                    // Set user principal in session
                }
                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Phase 0: Simple in-memory broker (single JVM)
        registry.enableSimpleBroker("/topic", "/queue");

        // Future: Replace with RabbitMQ STOMP relay for multi-instance
        // registry.enableStompBrokerRelay("/topic", "/queue")
        //     .setRelayHost("rabbitmq")
        //     .setRelayPort(61613);

        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
}
```

### 11.6 Connection Lifecycle

```
Client                          Server
  │                               │
  │── CONNECT (JWT in header) ──► │
  │                               │── Validate JWT
  │                               │── Store session (Redis)
  │◄── CONNECTED (sessionId) ────│
  │                               │
  │── SUBSCRIBE /topic/contest/X ─┤
  │                               │── Verify permission
  │◄── SUBSCRIBED ───────────────│
  │                               │
  │── SEND /app/quiz/submit ────► │── Process answer
  │                               │── Publish score update
  │◄── MESSAGE /topic/contest/X ──│
  │                               │
  │── DISCONNECT ────────────────►│── Cleanup session
  │                               │── Remove from Redis
```

### 11.7 Disconnect & Reconnection

- Server-side: On disconnect, remove session from Redis. Clean up any locks.
- Client-side: Exponential backoff reconnection (1s, 2s, 4s, 8s, max 30s)
- On reconnect: Re-subscribe to all topics, fetch latest state via REST

### 11.8 Single-Instance Limitation (Phase 0)

In Phase 0, WebSocket uses the in-memory `SimpleBroker`. This means:
- All clients MUST connect to the **same** application instance to receive real-time updates
- If running multiple instances behind a load balancer, sticky sessions are REQUIRED
- Cross-instance messages are NOT possible without RabbitMQ/Kafka

**Workaround for multi-instance in Phase 0:**
- Use sticky sessions at the load balancer level
- Or, poll for updates via REST as a fallback (polling interval: 5-10 seconds)
- Live contest leaderboard can fall back to Redis polling + periodic REST refresh

**Upgrade path:** Replace `enableSimpleBroker` with `enableStompBrokerRelay` pointed at RabbitMQ. No code changes in controllers/handlers.

---

## 12. Future AI Integration Plan

### 12.1 Architecture for AI

```
┌──────────────────────────────────────────────────────────────────┐
│                       AI MODULE                                   │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │ AI Quiz      │  │ AI Content   │  │ AI           │           │
│  │ Generation   │  │ Moderation   │  │ Recommendation │           │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘           │
│         │                 │                  │                   │
│  ┌──────┴──────────────────────┴──────────────────┐             │
│  │          AI Model Router (Abstraction)          │             │
│  │  • OpenAI / GPT-4    • Anthropic / Claude      │             │
│  │  • Google / Gemini   • Custom Models (future)   │             │
│  │  • Fallback Chain    • Circuit Breaker          │             │
│  └──────────────────────┬──────────────────────────┘             │
│                         │                                        │
│  ┌──────────────────────▼──────────────────────────┐             │
│  │              AI Infrastructure                    │           │
│  │  • Rate Limiter (API credits)                    │           │
│  │  • Token Usage Tracker                           │           │
│  │  • Caching (same prompt → same response)         │           │
│  │  • Async Processing (CompletableFuture / Queue)  │           │
│  │  • Audit Trail (prompt + response logging)       │           │
│  └──────────────────────────────────────────────────┘           │
└──────────────────────────────────────────────────────────────────┘
```

### 12.2 AI Quiz Generation Pipeline

```
1. User requests AI quiz generation
   POST /api/ai/quiz/generate
   { "topic": "Java Streams", "difficulty": "HARD", "count": 10 }

2. AIQuizGeneration entity created (status=PENDING)

3. QuizGenerationPipeline:
   a. Construct prompt with system instructions
   b. Send to AI Model Router → GPT-4
   c. Parse response (JSON with questions + answers + explanations)
   d. Validate: No duplicates, valid answers, difficulty appropriate
   e. Store in ai_quiz_generations (status=COMPLETED)
   f. Optionally auto-save as draft questions (status=REVIEW_NEEDED)

4. Admin reviews generated questions:
   Accept → Questions saved to questions table
   Reject → Deleted with feedback logged
   Edit → Modified and saved

5. User notified when generation is complete (WebSocket / notification)
```

### 12.3 AI Recommendation Engine

```
Input Signals:
├── User quiz history (categories, difficulty, performance)
├── Time spent per question
├── Contests participated
├️⃣ Badges/achievements
├── Streak patterns
├── Similar users (collaborative filtering)
└── Time of day / day of week (temporal patterns)

Output:
├── Recommended quizzes
├── Recommended categories to improve (weak areas)
├── Suggested difficulty level progression
├── Contest recommendations
└── Personalized study path

Storage:
├── ai_recommendations table
├── Redis cache (user-specific, TTL = 1 hour)
└── Refresh on: quiz completion, contest end, daily
```

### 12.4 AI Content Moderation Pipeline

```
1. Content created (question, comment, description)
2. ModerationPipeline triggered:
   a. Check against blocklist (regex patterns)
   b. Send to AI Model Router → text-moderation-latest
   c. Categories: hate, harassment, self-harm, sexual, violence
   d. Decision:
      - APPROVED (confidence > 0.95) → Auto-approve
      - FLAGGED (0.70 < confidence < 0.95) → Queue for human review
      - REJECTED (confidence < 0.70) → Block content
3. Logged in ai_content_moderations
4. Content creator notified if rejected/flagged
```

### 12.5 AI Chatbot Assistant (Future)

```
┌──────────────────────────────────────────────────────┐
│  Chat Interface (WebSocket-based)                    │
│                                                      │
│  /ws/chat  →  ChatController  →  ChatSessionManager  │
│                                        │             │
│  ┌────────────────────────────────────────────────┐  │
│  │  Context Builder                               │  │
│  │  • Current user stats / progress               │  │
│  │  • Recent quiz results                         │  │
│  │  • Platform docs / FAQ embeddings (Vector DB)  │  │
│  │  • Conversation history                        │  │
│  └────────────────────┬───────────────────────────┘  │
│                       │                              │
│  ┌────────────────────▼───────────────────────────┐  │
│  │  AI Model Router → GPT-4 / Claude              │  │
│  │  • System prompt with context                  │  │
│  │  • Function calling: create_quiz, get_stats    │  │
│  │  • Streaming response via WebSocket            │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

### 12.6 AI Tables (Pre-Created for Migration)

| Table | Purpose | Data Volume |
|-------|---------|-------------|
| `ai_quiz_generations` | Track AI-generated quiz requests and results | Low-Medium |
| `ai_content_moderations` | Log all AI moderation decisions | Medium-High |
| `ai_recommendations` | Persisted recommendation results | Medium |

> These tables already exist in the schema as a scaffold. The `ai` module packages are already structured with domain + application + infrastructure layers, ready for implementation.

### 12.7 AI Cost Control Strategy

| Measure | Implementation |
|---------|---------------|
| Rate Limiting | Max 5 AI generations per user per hour |
| Token Budget | Per-request token limits (prompt + response) |
| Caching | Same prompt → cached response (hash-based lookup) |
| Model Tiering | Simple tasks → cheaper/faster model; complex → premium |
| Usage Tracking | Per-user, per-feature token usage dashboard |
| Fallback | Circuit breaker → graceful degradation (static fallback) |

---

## 13. Scalability Bottlenecks and Solutions

### 13.1 Phase 0 Bottlenecks (MySQL + Redis + Local FS only)

| Bottleneck | Risk | Mitigation |
|-----------|------|------------|
| Leaderboard queries scanning millions of rows in MySQL | High | Redis Sorted Sets for live rankings; MySQL stores only historical snapshots |
| Full-text search on questions (no Elasticsearch) | Medium | MySQL FULLTEXT index with Boolean mode; limit results; paginate aggressively |
| File uploads stored on local disk (no S3) | Medium | Store outside app dir; configure max file size (10MB); separate uploads to `uploads/` |
| WebSocket stuck to single instance (SimpleBroker) | Medium | Sticky sessions at load balancer; OR REST polling fallback |
| Audit log writes on every request (DB writes) | Medium | Async `@Async` logging; batch inserts; archive old logs periodically |
| User statistics contention (many quiz completions) | Medium | Redis atomic increments; periodic sync to MySQL `user_statistics` |
| In-process event processing blocks main thread | Medium | `@Async` with dedicated thread pool; `CallerRunsPolicy` as backpressure |
| N+1 queries on quiz → questions → options | Medium | `@EntityGraph`, batch fetching (`@BatchSize`), DTO projections |
| Role/permission lookups on every authenticated request | Low | JWT contains permissions claim (no DB hit); Redis cache fallback |
| Question randomization with large datasets | Low | Cache question IDs per category in Redis; shuffle on app side |

### 13.2 Phase 0 Scaling Limits (Single JVM)

| Resource | Practical Limit | When to Scale |
|----------|---------------|---------------|
| Active users | ~5,000 concurrent | Add more JVM instances + sticky sessions |
| WebSocket connections | ~2,000 per instance | Upgrade to RabbitMQ STOMP relay for multi-instance |
| Questions per category | ~50,000 | Add MySQL FULLTEXT indexes; later migrate to Elasticsearch |
| Quiz submissions per second | ~100 req/s | Add read replicas + Redis write-behind |
| File storage | 50 GB local | Migrate to S3/MinIO with CDN |
| Audit log volume | 10M rows | Partition by month; archive to cold storage |
| Leaderboard entries per contest | 100,000 | Limit contest size; Redis Sorted Sets handle this well |

### 13.3 When to Add Paid/Cloud Infrastructure

| Trigger | Infrastructure to Add |
|---------|---------------------|
| Multi-instance WebSocket needed | RabbitMQ STOMP relay |
| Search latency > 500ms | Elasticsearch |
| File storage > 50 GB | S3/MinIO |
| Logs > 5 GB/day | ELK/Loki stack |
| Active users > 10,000 | Read replicas, connection pooling tuning |
| Quiz submissions > 500/sec | Kafka for event buffering |

### 13.4 Network/Infrastructure Bottlenecks

| Bottleneck | Risk | Solution |
|-----------|------|----------|
| SSL termination overhead | Low | Load balancer terminates SSL |
| Database connection pool exhaustion | Medium | Connection pooling (HikariCP), max pool size = 30 per instance |
| Inter-service latency (post-migration) | Medium | gRPC for internal calls; async where possible |

### 13.5 Horizontal Scaling Strategy

```
Frontend                Load Balancer           App Instances              Data Tier
                        (Round Robin)
                                        
┌──────────┐                 │              ┌──────────────┐          ┌──────────┐
│  Users    │────────────────┤              │ App Instance 1 │─────────►  MySQL   │
└──────────┘                 ├──────────────┤ App Instance 2 ├─────────► (Primary)│
                             │              │ App Instance 3 │          └──────────┘
                             │              └──────┬───────┬┘                │
                             │      ┌───────────────┤       │          ┌─────▼─────┐
                             │      │          ┌────▼───┐ ┌─▼─────┐   │  MySQL    │
                             │      │          │ Redis  │ │RabbitMQ│   │ (Replica) │
                             │      │          │Cluster │ │Cluster │   └───────────┘
                             │      │          └────────┘ └───────┘
```

### 13.6 Key Metrics to Monitor

| Metric | Alert Threshold | Action |
|--------|----------------|--------|
| P99 API Latency | > 500ms | Check DB queries, Redis latency |
| DB Connection Pool Usage | > 80% | Increase pool or add read replicas |
| Redis Memory Usage | > 80% | Increase memory or adjust TTL |
| CPU Utilization | > 75% for 5 min | Add horizontal instances |
| WebSocket Connection Count | > 5000/instance | Add more instances |
| Error Rate (5xx) | > 1% | Check logs, rollback if deployment-related |
| Active Users | > 80% of estimated | Scale up cluster |
| Quiz Submission Rate | > 100/sec | Async queue, scale out |

---

## 14. Production Deployment Recommendations

### 14.1 Infrastructure (Phase 0 — Self-Hosted / VPS)

| Component | Recommendation | Rationale |
|-----------|---------------|-----------|
| Deployment | Single JAR + systemd service | Simplest possible; Docker optional |
| App Server | Spring Boot embedded Tomcat (1 JVM) | No external web server needed |
| MySQL | MySQL 8.0 (local install or Docker) | Primary database |
| Redis | Redis 7.x (local install or Docker) | Caching, leaderboards, rate limiting |
| File Storage | Local filesystem (`uploads/` directory) | Simple, no cloud dependency |
| Process Manager | systemd / supervisord | Auto-restart on crash |
| Build | Maven wrapper (`mvnw.cmd`) | No CI server needed initially |

**Future upgrades** (when scaling):
- Docker + Docker Compose for container orchestration
- Kubernetes when multi-node needed
- Managed RDS for MySQL, ElastiCache for Redis
- S3/MinIO for file storage
- RabbitMQ for async messaging

### 14.2 CI/CD Pipeline

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│   Code   │   │   Build  │   │   Test   │   │  Deploy  │   │  Monitor │
│  Commit  │──►│  (Maven) │──►│ (JUnit + │──►│ (K8s     │──►│  (Grafana│
│          │   │          │   │  Integ.) │   │  Rollout)│   │  + Alerts)│
└──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘
```

| Stage | Tools | Actions |
|-------|-------|---------|
| Code | GitHub/GitLab | Feature branch → PR → code review → merge to main |
| Build | Jenkins/GitHub Actions | `./mvnw clean verify`, SonarQube analysis, Docker image build |
| Test | JUnit, Testcontainers | Unit tests, integration tests (MySQL + Redis containers), contract tests |
| Staging | K8s namespace | Deploy to staging, run smoke tests, performance tests |
| Production | ArgoCD / Spinnaker | Canary deployment (10% → 50% → 100%); automatic rollback on error |
| Monitor | Prometheus + Grafana | Dashboards, alerts, SLI/SLO tracking |

### 14.3 Environment Configuration

| Environment | Purpose | DB | Replicas | Resources |
|-------------|---------|----|----------|-----------|
| Local | Developer machine | H2/MySQL Docker | 1 | 2 CPU / 4 GB |
| Dev | Integration testing | MySQL single | 1-2 | 2 CPU / 4 GB |
| Staging | Pre-production validation | MySQL single + replica | 2-3 | 4 CPU / 8 GB |
| Production | Live traffic | MySQL Multi-AZ + 2 replicas | 3-6 (HPA) | 4-8 CPU / 8-16 GB |

### 14.4 Application Configuration (Externalized)

| Config Source | Example | Purpose |
|--------------|---------|---------|
| Environment Variables | `JWT_SECRET`, `DB_URL`, `REDIS_HOST` | Secrets, connection strings |
| `application.properties` + profiles | `application-dev.yml`, `application-prod.yml` | Profile-specific configuration |
| `.env` file (dev only) | Local development override | Convenience, NOT for production |
| Command-line args | `--server.port=8083` | Runtime overrides |

### 14.5 Backup & Disaster Recovery (Phase 0)

| Component | Backup Strategy | RPO | RTO |
|-----------|----------------|-----|-----|
| MySQL | `mysqldump` daily cron + binary log | 24 hours | 2 hours |
| Redis | RDB snapshots every 5 min (persistent mode) | 5 min | 15 min |
| File Store | `rsync` / `robocopy` to backup disk daily | 24 hours | 4 hours |
| Application | Maven build artifact (`target/*.jar`) | Per release | 30 min |
| Configuration | Git + `.env` template | Per commit | 15 min |

**Upgrade path:** MySQL binary log replication → managed RDS with Multi-AZ automated failover.

### 14.6 Observability (Phase 0)

```
┌────────────────────────────────────────────────┐
│                  Log Files                      │
│  ┌──────────────┐  ┌──────────────────────┐    │
│  │ app_out.log  │  │ app_err.log          │    │
│  │ (stdout)     │  │ (stderr + stacktrace)│    │
│  └──────────────┘  └──────────────────────┘    │
│                                                 │
│  ┌──────────────────────────────────────────┐   │
│  │ Spring Boot Actuator Endpoints            │   │
│  │ • /actuator/health    → Liveness check   │   │
│  │ • /actuator/info      → Build metadata   │   │
│  │ • /actuator/metrics   → JVM/DB/cache     │   │
│  │ • /actuator/loggers   → Dynamic logging  │   │
│  └──────────────────────────────────────────┘   │
└────────────────────────────────────────────────┘

[Prometheus / Grafana / Loki — Future when more instances are added]
```

### 14.7 Health Check Endpoints

| Endpoint | Check | Frequency |
|----------|-------|-----------|
| `/actuator/health` | Liveness + Readiness | 10s |
| `/actuator/health/db` | Database connectivity | 30s |
| `/actuator/health/redis` | Redis connectivity | 30s |
| `/actuator/info` | Build info, git commit, version | On demand |
| `/actuator/metrics` | JVM, request, DB pool metrics | On demand |
| `/actuator/loggers` | Dynamic log level changes | On demand |

### 14.8 Performance Benchmarks (Phase 0 Targets — Single JVM, MySQL + Redis)

| Operation | Target P99 | Target P95 | Target P50 |
|-----------|-----------|-----------|-----------|
| User Login | 800ms | 400ms | 150ms |
| Quiz Creation | 1.5s | 800ms | 300ms |
| Quiz Submission + Scoring | 2.5s | 1.5s | 500ms |
| Leaderboard Top 100 (Redis) | 100ms | 50ms | 10ms |
| User Profile | 500ms | 200ms | 60ms |
| Question Search (MySQL FULLTEXT) | 1s | 500ms | 150ms |
| File Upload (1MB, local FS) | 2s | 1s | 300ms |
| WebSocket Message Delivery | 150ms | 80ms | 20ms |
| Notification Delivery (in-app) | 300ms | 150ms | 50ms |
| Category/Quiz Listing (paginated) | 500ms | 200ms | 50ms |
| Daily Contest Creation (scheduled) | 2s | 1s | 300ms |
| Audit Log Write | 100ms | 50ms | 10ms |

> **Note:** These targets assume a development/early-production setup. Add Redis caching for frequently accessed data to stay within bounds.

### 14.9 Technology Stack (Phase 0 Implementation)

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| Language | Java | 21 | Platform |
| Framework | Spring Boot | 4.0.2 | Web, Security, Data JPA |
| Database | MySQL | 8.0 | Primary relational store |
| Cache + Leaderboard | Redis | 7.x | Caching, sorted sets, rate limiting |
| ORM | Hibernate + JPA | 6.x | Object-relational mapping |
| DB Migration | Flyway | 10.x | Schema versioning |
| Build | Maven | 3.9+ | Build & dependency management |
| WebSocket | Spring WebSocket + STOMP | Built-in | Real-time updates |
| File Storage | Local filesystem | N/A | Uploaded files |
| Search | MySQL FULLTEXT indexes | Built-in | Basic question search |
| Event Bus | Spring `ApplicationEventPublisher` + `@Async` | Built-in | In-process domain events |
| Monitoring | Spring Boot Actuator + Logback | Built-in | Health checks, metrics, logging |
| Auth | JWT (jjwt) + BCrypt | 0.11.5 | Stateless auth |
| Testing | JUnit 5, Testcontainers, Mockito | Latest | Unit + integration tests |
| AI | Placeholder module only | N/A | Scaffolded for future |

### 14.10 Future Upgrades (When Needed)

| Trigger | Upgrade Path |
|---------|-------------|
| Need real search | Add Elasticsearch |
| 2+ app instances | Add RabbitMQ STOMP relay for WebSocket |
| Need async processing across instances | Add RabbitMQ/Kafka for domain events |
| File storage growing | Add S3/MinIO with CDN |
| Monitoring needed | Add Prometheus + Grafana + Loki |
| More users | Add MySQL read replicas, connection pooling tuning |
| Multi-service | Extract services per migration plan (Section 9) |

---

> **Document Version:** 1.0
> **Last Updated:** 2026-06-14
> **Author:** System Architecture Team
