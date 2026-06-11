# QuizApp

A Spring Boot REST backend for creating and running multiple-choice quizzes with **RBAC** (Role-Based Access Control) supporting unlimited roles and granular permissions.

---

## Features

- JWT-based authentication with access + refresh tokens (token rotation)
- **RBAC**: Each user has a single role, roles bundle multiple permissions (all DB-driven)
- 3 default roles: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_SUPER_ADMIN` — seedable, extensible at runtime
- 22 granular permissions (e.g. `question:create`, `quiz:attempt`, `user:manage`)
- CRUD for questions, categories, quizzes, roles, and permissions
- Random quiz generation from category with scoring
- Paginated endpoints with consistent metadata
- Centralized error handling with JSON responses

---

## Tech Stack

| Layer        | Technology                              |
|-------------|-----------------------------------------|
| Framework   | Spring Boot 4.0.2 (Web, Security, Data JPA) |
| Language    | Java 21                                 |
| Database    | MySQL 8                                 |
| Auth        | JWT (jjwt 0.11.5)                       |
| Build       | Maven (wrapper: `mvnw.cmd`)             |

---

## Table of Contents

- [Getting Started](#getting-started)
- [Authentication](#authentication)
- [RBAC Architecture](#rbac-architecture)
- [API Reference](#api-reference)
  - [Auth](#1-auth-base-path-auth)
  - [Questions](#2-questions-base-path-question)
  - [Quiz & Categories](#3-quiz--categories-base-path-quiz)
  - [Roles & Permissions](#4-roles--permissions-base-path-roles)
- [Response Wrapper](#response-wrapper--error-format)
- [Seeded Data](#seeded-data)
- [Demo Requests](#demo-requests)

---

## Getting Started

```powershell
# Run with Maven wrapper
.\mvnw.cmd spring-boot:run
```

Default port: `8083` (configurable in `application.properties`).

The database tables and seed data are created **automatically** on first run via Hibernate DDL auto-update + `DataInitializer`.

---

## Authentication

1. **Register** or **Login** to receive an `authToken` (access) and `refreshToken`.
2. Include the access token in the `Authorization` header for all protected requests:

```
Authorization: Bearer <authToken>
```

3. When the access token expires, call `POST /auth/refresh` with your refresh token to get new tokens (old refresh token is revoked — token rotation).

**Query params for registration:**

| Param   | Type    | Default | Description                                      |
|---------|---------|---------|--------------------------------------------------|
| `admin` | boolean | `false` | If `true`, assigns `ROLE_ADMIN` instead of `ROLE_USER` |
| `role`  | String   | (empty) | Custom role name (e.g. `?role=ROLE_MODERATOR`) |

---

## RBAC Architecture

```
User ──(ManyToMany)──> Role ──(ManyToMany)──> Permission
```

### Permission Catalog (22)

| Permission          | ROLE_USER | ROLE_ADMIN | ROLE_SUPER_ADMIN |
|--------------------|:---------:|:----------:|:----------------:|
| `question:read`    | ✓         | ✓          | ✓                |
| `question:create`  |           | ✓          | ✓                |
| `question:update`  |           | ✓          | ✓                |
| `question:delete`  |           | ✓          | ✓                |
| `quiz:read`        | ✓         | ✓          | ✓                |
| `quiz:create`      |           | ✓          | ✓                |
| `quiz:update`      |           | ✓          | ✓                |
| `quiz:delete`      |           | ✓          | ✓                |
| `quiz:attempt`     | ✓         | ✓          | ✓                |
| `category:read`    | ✓         | ✓          | ✓                |
| `category:create`  |           | ✓          | ✓                |
| `category:update`  |           | ✓          | ✓                |
| `category:delete`  |           | ✓          | ✓                |
| `user:read`        |           | ✓          | ✓                |
| `user:create`      |           | ✓          | ✓                |
| `user:update`      |           | ✓          | ✓                |
| `user:delete`      |           | ✓          | ✓                |
| `user:manage`      |           |            | ✓                |
| `role:read`        |           | ✓          | ✓                |
| `role:create`      |           |            | ✓                |
| `role:update`      |           |            | ✓                |
| `role:delete`      |           |            | ✓                |

---

## API Reference

### 1. Auth (base path: `/auth`)

---

#### POST `/auth/register`

Register a new user.

**Required permission:** Public (no auth)

**Request body:**
```json
{
  "username": "johndoe",
  "password": "securepass",
  "email": "john@example.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St"
}
```

**Query params:** `?admin=false` (optional), `?role=ROLE_X` (optional)

**Response `201 Created`:**
```json
{
  "statusCode": 201,
  "message": "Registration successful!",
  "response": {
    "username": "johndoe",
    "role": "ROLE_USER",
    "permissions": ["question:read", "quiz:read", "quiz:attempt", "category:read"],
    "email": "john@example.com",
    "phoneNumber": "1234567890",
    "address": "123 Main St",
    "profilePicture": "",
    "authToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### POST `/auth/login`

Authenticate an existing user.

**Required permission:** Public (no auth)

**Request body:**
```json
{
  "username": "johndoe",
  "password": "securepass"
}
```

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Login successful!",
  "response": {
    "username": "johndoe",
    "role": "ROLE_USER",
    "permissions": ["question:read", "quiz:read", "quiz:attempt", "category:read"],
    "email": "john@example.com",
    "phoneNumber": "1234567890",
    "address": "123 Main St",
    "profilePicture": "",
    "authToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### POST `/auth/refresh`

Refresh the access token using a valid refresh token.

**Required permission:** Public (uses refresh token)

**Request body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Token refreshed successfully!",
  "response": {
    "username": "johndoe",
    "role": "ROLE_USER",
    "permissions": ["question:read", "quiz:read", "quiz:attempt", "category:read"],
    "email": "john@example.com",
    "phoneNumber": "1234567890",
    "address": "123 Main St",
    "profilePicture": "",
    "authToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

#### POST `/auth/logout`

Revoke the refresh token and log out.

**Required permission:** `isAuthenticated()`

**Request body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Logout successful!",
  "response": null
}
```

---

### 2. Questions (base path: `/question`)

> **Note:** All question read responses intentionally exclude `rightAnswer` to prevent answer exposure.

---

#### GET `/question/all`

Return all questions (no pagination).

**Required permission:** `question:read`

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Questions fetched successfully!",
  "response": [
    {
      "id": 1,
      "questionTitle": "What is Java?",
      "option1": "A programming language",
      "option2": "A coffee",
      "option3": "An island",
      "option4": "A car",
      "difficultyLevel": "EASY",
      "categoryId": 1,
      "categoryName": "Programming"
    }
  ]
}
```

---

#### GET `/question/all/paged?page=0&size=10&sortBy=id`

Return paginated questions with metadata.

**Required permission:** `question:read`

**Query params:** `page` (default: 0), `size` (default: 10), `sortBy` (default: `id`)

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Questions fetched successfully!",
  "response": [
    {
      "id": 1,
      "questionTitle": "What is Java?",
      "option1": "A programming language",
      "option2": "A coffee",
      "option3": "An island",
      "option4": "A car",
      "difficultyLevel": "EASY",
      "categoryId": 1,
      "categoryName": "Programming"
    }
  ],
  "meta": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

#### GET `/question/category/{categoryName}`

Return questions filtered by category name.

**Required permission:** `question:read`

**Path params:** `categoryName` (String)

**Response `200 OK`:** Same as `/question/all`

---

#### GET `/question/category-id/{categoryId}`

Return questions filtered by category ID.

**Required permission:** `question:read`

**Path params:** `categoryId` (Integer)

**Response `200 OK`:** Same as `/question/all`

---

#### POST `/question/add`

Create a new question.

**Required permission:** `question:create`

**Request body:**
```json
{
  "questionTitle": "What is Java?",
  "option1": "A programming language",
  "option2": "A coffee",
  "option3": "An island",
  "option4": "A car",
  "rightAnswer": "A programming language",
  "difficultyLevel": "EASY",
  "categoryId": 1
}
```

**Response `201 Created`:**
```json
{
  "statusCode": 201,
  "message": "Question added successfully!",
  "response": {
    "id": 1,
    "questionTitle": "What is Java?",
    "option1": "A programming language",
    "option2": "A coffee",
    "option3": "An island",
    "option4": "A car",
    "difficultyLevel": "EASY",
    "categoryId": 1,
    "categoryName": "Programming"
  }
}
```

---

#### DELETE `/question/delete/{id}`

Delete a question by its ID.

**Required permission:** `question:delete`

**Path params:** `id` (Integer)

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Question deleted successfully!",
  "response": {
    "id": 1,
    "questionTitle": "What is Java?",
    "option1": "A programming language",
    "option2": "A coffee",
    "option3": "An island",
    "option4": "A car",
    "difficultyLevel": "EASY",
    "categoryId": 1,
    "categoryName": "Programming"
  }
}
```

---

### 3. Quiz & Categories (base path: `/quiz`)

---

#### POST `/quiz/create`

Create a quiz by selecting random questions from a category.

**Required permission:** `quiz:create`

**Request body:**
```json
{
  "categoryId": 1,
  "numQuestions": 5,
  "title": "Java Basics Quiz"
}
```

**Response `201 Created`:**
```json
{
  "statusCode": 201,
  "message": "Quiz created successfully!",
  "response": {
    "id": 1,
    "title": "Java Basics Quiz",
    "questions": [
      {
        "id": 1,
        "questionTitle": "What is Java?",
        "option1": "A programming language",
        "option2": "A coffee",
        "option3": "An island",
        "option4": "A car"
      }
    ]
  }
}
```

> **Note:** `rightAnswer` is excluded from quiz question responses.

---

#### GET `/quiz/{id}/questions`

Get quiz questions (correct answers excluded).

**Required permission:** `quiz:read`

**Path params:** `id` (Integer)

**Response `200 OK`:** Same shape as `/quiz/create` response.

---

#### POST `/quiz/{id}/submit`

Submit answers and receive a score breakdown.

**Required permission:** `quiz:attempt`

**Path params:** `id` (Integer)

**Request body:**
```json
[
  { "id": 1, "response": "A programming language" },
  { "id": 2, "response": "1995" }
]
```

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Quiz submitted successfully!",
  "response": {
    "quizId": 1,
    "quizTitle": "Java Basics Quiz",
    "totalQuestions": 5,
    "correctAnswers": 3,
    "wrongAnswers": 2,
    "scorePercentage": 60.0
  }
}
```

---

#### GET `/quiz/categories?page=0&size=10&sortBy=id`

Get paginated list of categories.

**Required permission:** `category:read`

**Query params:** `page` (default: 0), `size` (default: 10), `sortBy` (default: `id`)

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Categories fetched successfully!",
  "response": [
    {
      "id": 1,
      "categoryName": "Programming"
    }
  ],
  "meta": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 3,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

#### POST `/quiz/category/add`

Add a new category.

**Required permission:** `category:create`

**Request body:**
```json
{
  "categoryName": "Science"
}
```

**Response `201 Created`:**
```json
{
  "statusCode": 201,
  "message": "Category added successfully!",
  "response": {
    "id": 4,
    "categoryName": "Science"
  }
}
```

---

#### PUT `/quiz/category/edit/{id}`

Edit an existing category name.

**Required permission:** `category:update`

**Path params:** `id` (Integer)

**Request body:**
```json
{
  "categoryName": "Natural Science"
}
```

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Category edited successfully!",
  "response": {
    "id": 4,
    "categoryName": "Natural Science"
  }
}
```

---

#### DELETE `/quiz/category/delete/{id}`

Delete a category by its ID.

**Required permission:** `category:delete`

**Path params:** `id` (Integer)

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Category deleted successfully!",
  "response": {
    "id": 4,
    "categoryName": "Natural Science"
  }
}
```

---

### 4. Roles & Permissions (base path: `/roles`)

---

#### GET `/roles`

List all roles with their associated permissions.

**Required permission:** `role:read`

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Roles fetched successfully!",
  "response": [
    {
      "id": 1,
      "name": "ROLE_USER",
      "description": "Regular user",
      "permissions": ["question:read", "quiz:read", "quiz:attempt", "category:read"]
    },
    {
      "id": 2,
      "name": "ROLE_ADMIN",
      "description": "Administrator",
      "permissions": ["question:read", "question:create", "question:update", "question:delete", "quiz:read", "quiz:create", "quiz:update", "quiz:delete", "quiz:attempt", "category:read", "category:create", "category:update", "category:delete", "user:read", "user:create", "user:update", "user:delete", "role:read"]
    },
    {
      "id": 3,
      "name": "ROLE_SUPER_ADMIN",
      "description": "Super Administrator",
      "permissions": ["question:read", "question:create", "question:update", "question:delete", "quiz:read", "quiz:create", "quiz:update", "quiz:delete", "quiz:attempt", "category:read", "category:create", "category:update", "category:delete", "user:read", "user:create", "user:update", "user:delete", "user:manage", "role:read", "role:create", "role:update", "role:delete"]
    }
  ]
}
```

---

#### GET `/roles/{id}`

Get a specific role by its ID.

**Required permission:** `role:read`

**Path params:** `id` (Integer)

**Response `200 OK:`** Single `RolesResponse` object (same shape as above).

---

#### POST `/roles`

Create a new role with the specified permissions.

**Required permission:** `role:create`

**Request body:**
```json
{
  "name": "ROLE_MODERATOR",
  "description": "Can moderate content",
  "permissionNames": ["question:read", "question:update", "quiz:read"]
}
```

**Response `201 Created`:** Single `RolesResponse` object.

---

#### PUT `/roles/{id}`

Update an existing role's name, description, and/or permissions.

**Required permission:** `role:update`

**Path params:** `id` (Integer)

**Request body:** (same shape as POST — only provided fields get updated)
```json
{
  "description": "Moderator with advanced permissions",
  "permissionNames": ["question:read", "question:update", "question:delete", "quiz:read"]
}
```

**Response `200 OK`:** Single `RolesResponse` object.

---

#### DELETE `/roles/{id}`

Delete a role by its ID.

**Required permission:** `role:delete`

**Path params:** `id` (Integer)

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Role deleted successfully!",
  "response": null
}
```

---

#### POST `/roles/{roleId}/permissions`

Add permissions to an existing role.

**Required permission:** `role:update`

**Path params:** `roleId` (Integer)

**Request body:** (raw JSON array)
```json
["report:read", "report:export"]
```

**Response `200 OK`:** Single `RolesResponse` object with updated permission set.

---

#### DELETE `/roles/{roleId}/permissions`

Remove permissions from an existing role.

**Required permission:** `role:update`

**Path params:** `roleId` (Integer)

**Request body:** (raw JSON array)
```json
["report:read"]
```

**Response `200 OK`:** Single `RolesResponse` object.

---

#### GET `/roles/permissions`

List all available permissions in the system.

**Required permission:** `role:read`

**Response `200 OK`:**
```json
{
  "statusCode": 200,
  "message": "Permissions fetched successfully!",
  "response": [
    {
      "id": 1,
      "name": "question:read",
      "description": "View questions"
    },
    {
      "id": 2,
      "name": "question:create",
      "description": "Create questions"
    }
  ]
}
```

---

## Response Wrapper & Error Format

### Success Response Structure

```json
{
  "statusCode": 200,
  "message": "Operation message",
  "response": { ... },
  "meta": {              // only present for paginated endpoints
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### Error Response Structure

```json
{
  "statusCode": 4xx,
  "message": "Error description",
  "response": null
}
```

| HTTP Status | Error Scenario                          |
|-------------|-----------------------------------------|
| `400`       | Validation failure (invalid fields)     |
| `401`       | Missing/invalid JWT token               |
| `403`       | Insufficient permissions                |
| `404`       | Resource not found                      |
| `409`       | Duplicate resource (e.g. username, role name) |
| `500`       | Internal server error                   |

---

## Seeded Data

On first startup, `DataInitializer` creates:

| Item | Details |
|------|---------|
| **22 permissions** | All permissions listed in the catalog above |
| **3 roles** | `ROLE_USER`, `ROLE_ADMIN`, `ROLE_SUPER_ADMIN` with appropriate permission sets |
| **1 super admin user** | `superadmin` / `superadmin123` (has `ROLE_SUPER_ADMIN` = all 22 permissions) |

### Creating a Super Admin via API

```bash
curl -X POST "http://localhost:8083/auth/register?role=ROLE_SUPER_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{ "username":"myadmin", "password":"admin123", "email":"a@b.com", "phoneNumber":"0", "address":"x" }'
```

### Adding a New Role at Runtime

```bash
# 1. Create role
curl -X POST http://localhost:8083/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <superAdminToken>" \
  -d '{ "name":"ROLE_MODERATOR", "description":"Moderator", "permissionNames":["question:read","question:update"] }'

# 2. Assign to a user (register with this role)
curl -X POST "http://localhost:8083/auth/register?role=ROLE_MODERATOR" \
  -H "Content-Type: application/json" \
  -d '{ "username":"mod", "password":"mod123", "email":"m@b.com", "phoneNumber":"0", "address":"x" }'
```

---

## Demo Requests

### 1. Register a User

```bash
curl -X POST http://localhost:8083/auth/register \
  -H "Content-Type: application/json" \
  -d '{ "username":"alice", "password":"alice123", "email":"alice@test.com", "phoneNumber":"9876543210", "address":"Somewhere" }'
```

### 2. Login

```bash
curl -X POST http://localhost:8083/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "username":"alice", "password":"alice123" }'
```

### 3. Get Paged Questions (authenticated)

```bash
curl -X GET "http://localhost:8083/question/all/paged?page=0&size=10&sortBy=id" \
  -H "Authorization: Bearer <authToken>"
```

### 4. Create Question (requires `question:create`)

```bash
curl -X POST http://localhost:8083/question/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <adminToken>" \
  -d '{ "questionTitle":"What is 2+2?", "option1":"3", "option2":"4", "option3":"5", "option4":"6", "rightAnswer":"4", "difficultyLevel":"EASY", "categoryId":1 }'
```

### 5. Create Quiz (requires `quiz:create`)

```bash
curl -X POST http://localhost:8083/quiz/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <adminToken>" \
  -d '{ "categoryId":1, "numQuestions":5, "title":"Quick Quiz" }'
```

### 6. Submit Quiz Answers (requires `quiz:attempt`)

```bash
curl -X POST http://localhost:8083/quiz/1/submit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <authToken>" \
  -d '[{ "id":1, "response":"A programming language" }, { "id":2, "response":"1995" }]'
```

### 7. List All Roles (requires `role:read`)

```bash
curl -X GET http://localhost:8083/roles \
  -H "Authorization: Bearer <adminToken>"
```

### 8. Create a New Role (requires `role:create`)

```bash
curl -X POST http://localhost:8083/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <superAdminToken>" \
  -d '{ "name":"ROLE_CONTRIBUTOR", "description":"Can contribute questions", "permissionNames":["question:create","question:read","quiz:read"] }'
```
