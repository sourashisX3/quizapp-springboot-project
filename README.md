# QuizApp

A Spring Boot REST backend for creating and running multiple-choice quizzes.

This repository contains the backend for a quiz application providing:
- User authentication (register, login, refresh, logout)
- CRUD operations for Questions and Categories (admin-only where applicable)
- Creation of quizzes from categories and submission/scoring of answers
- Consistent API response wrapper and pagination support

This README documents how to run the application locally, the high-level API structure, authentication, response shapes, and example requests you can use when testing.

Table of contents

- Project overview
- Features
- Tech stack
- Important modules / controllers
- Run & test locally (Windows PowerShell)
- Authentication
- API summary (Auth, Questions, Quiz & Categories)
- Response wrapper & error format
- Example requests (curl / PowerShell)
- Contributing

Project overview

The backend is implemented with Spring Boot and exposes REST endpoints to manage quizzes and questions and to let users take quizzes. It intentionally hides correct answers in read endpoints (so clients cannot see the right answers when fetching quiz questions).

Features

- JWT-based authentication with access and refresh tokens
- Role-based access: ADMIN and USER
- CRUD operations for questions and categories (ADMIN only where appropriate)
- Create quizzes by selecting random questions from a category
- Submit quiz answers and receive a score breakdown
- Paginated endpoints with pagination metadata

Tech stack

- Java 17+ (as configured in the project)
- Spring Boot (Web, Security, Data JPA)
- Maven (wrapper included: `mvnw`, `mvnw.cmd`)

Important modules / controllers

- `com.sourashis.quizapp.modules.auth.controller.AuthenticationController` — /auth
- `com.sourashis.quizapp.modules.question.controller.QuestionController` — /question
- `com.sourashis.quizapp.modules.quiz.controller.QuizController` — /quiz

Run & test locally (Windows PowerShell)

From the project root (where `mvnw.cmd` is located):

```powershell
# Run with the Maven wrapper
.\mvnw.cmd spring-boot:run

# Or build and run the jar
.\mvnw.cmd clean package
java -jar target\*.jar
```

Default server port: 8080 (unless overridden in `src/main/resources/application.properties`).

Authentication

- The API uses JWT-based tokens. Successful authentication returns an access token (`authToken`) and a `refreshToken`.
- To access protected endpoints, include the header:

  Authorization: Bearer <authToken>

- Role-based access is enforced using Spring Security annotations. Typical restrictions:
  - ADMIN: create/edit/delete categories and questions, create quizzes
  - ADMIN or USER: fetch questions, take quizzes, submit answers

API summary

All responses are wrapped in an `ApiResponseWrapper<T>` which contains a status code, message, optional payload in `response`, and optional pagination metadata in `meta` for paged endpoints.

1) Auth (base path: `/auth`)

- POST `/auth/register?admin=false` — Register a new user. `admin=true` may create an admin user depending on configuration.
  - Request body: AuthenticationRequest
    - username, password, email, phoneNumber, address
  - Response: ApiResponseWrapper<AuthenticationResponse> (201 Created)

- POST `/auth/login` — Login with username and password.
  - Request body: { username, password }
  - Response: ApiResponseWrapper<AuthenticationResponse>

- POST `/auth/refresh` — Refresh access token using a refresh token.
  - Request body: { refreshToken }
  - Response: ApiResponseWrapper<AuthenticationResponse>

- POST `/auth/logout` — Revoke refresh token / logout.
  - Request body: { refreshToken }
  - Response: ApiResponseWrapper<Void>

2) Questions (base path: `/question`)

- GET `/question/all` — Return all questions (no pagination). Roles: ADMIN, USER
- GET `/question/all/paged?page=0&size=10&sortBy=id` — Return paged questions with `meta`.
- GET `/question/category/{categoryName}` — Questions filtered by category name.
- GET `/question/category-id/{categoryId}` — Questions filtered by category id.
- POST `/question/add` — Create a question (ADMIN only). Request body: QuestionRequest
- DELETE `/question/delete/{id}` — Delete a question (ADMIN only)

Note: Question read DTOs intentionally exclude the correct answer to prevent exposure.

3) Quiz & Category (base path: `/quiz`)

- POST `/quiz/create` — Create a quiz by selecting random questions from a category (ADMIN only)
  - Request: QuizRequest { categoryId, numQuestions, title }
  - Response: ApiResponseWrapper<QuizResponse> (201 Created)

- GET `/quiz/{id}/questions` — Get quiz questions (correct answers excluded)
- POST `/quiz/{id}/submit` — Submit answers; receives score breakdown (ADMIN/USER)
  - Request: List<SubmitAnswerRequest> [{ id, response }, ...]
  - Response: ApiResponseWrapper<QuizScoreResponse>

- GET `/quiz/categories?page=0&size=10&sortBy=id` — Get paginated categories
- POST `/quiz/category/add` — Add category (ADMIN only)
- PUT `/quiz/category/edit/{id}` — Edit category (ADMIN only)
- DELETE `/quiz/category/delete/{id}` — Delete category (ADMIN only)

Response wrapper & error format

The API standardizes responses using `ApiResponseWrapper<T>` with the following JSON fields:

- statusCode: integer
- message: string
- response: T (may be null)
- meta: PaginationMeta (present for paged responses)

PaginationMeta fields:
- currentPage, pageSize, totalElements, totalPages, hasNext, hasPrevious

Errors

Errors are handled centrally and return an `ApiResponseWrapper<Void>` with an appropriate HTTP status code and message. Example shapes:

- Validation error (400): { statusCode: 400, message: "...validation failed...", response: null }
- Access denied (403): { statusCode: 403, message: "Access Denied - ...", response: null }

Example requests

1) Login (PowerShell/curl)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "username":"admin", "password":"adminpass" }'
```

2) Get paged questions (replace <authToken> with a valid token)

```bash
curl -X GET "http://localhost:8080/question/all/paged?page=0&size=10&sortBy=id" \
  -H "Authorization: Bearer <authToken>"
```

3) Create a quiz (ADMIN only)

```bash
curl -X POST http://localhost:8080/quiz/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <authToken>" \
  -d '{ "categoryId": 1, "numQuestions": 10, "title": "Sample Quiz" }'
```

4) Submit quiz answers

```bash
curl -X POST http://localhost:8080/quiz/1/submit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <authToken>" \
  -d '[{ "id": 123, "response": "option1" }, { "id": 124, "response": "option3" }]' 
```

Contributing

If you'd like to contribute, please fork the repository and open a pull request. Include unit tests for new features or bug fixes where applicable.

License

This project does not include a license file in the repository. Add a LICENSE if you want to open-source it.
