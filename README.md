# QuizApp

A Spring Boot REST backend for managing multiple-choice quizzes and questions.

This README documents the currently implemented API endpoints, request/response shapes (DTOs), authentication and error formats, and example requests you can use when testing the application locally.

---

High-level plan

- Collect all controllers and DTOs implemented in the codebase and list endpoints with HTTP methods, request/response shapes and sample JSON
- Include authentication information and example Authorization header
- Describe the ApiResponseWrapper and PaginationMeta shapes used across responses
- Add run/test instructions for Windows PowerShell

Checklist

- [x] Document Auth endpoints and DTOs
- [x] Document Question endpoints and DTOs
- [x] Document Quiz & Category endpoints and DTOs
- [x] Show ApiResponseWrapper and PaginationMeta shapes
- [x] Add example requests and responses
- [x] Provide run instructions

---

Table of contents

- Project overview
- Run & test locally
- Authentication
- API endpoints (Auth, Questions, Quiz & Categories)
- Response wrapper and error format
- Examples (curl / JSON)

---

Project overview

This project exposes a REST API for:
- User authentication (register/login/refresh/logout)
- CRUD operations for Questions
- Creating quizzes with random questions for a category, submitting answers and scoring
- Managing Categories used to group questions

Implemented modules (relevant controllers)
- `com.sourashis.quizapp.modules.auth.controller.AuthenticationController` — /auth
- `com.sourashis.quizapp.modules.question.controller.QuestionController` — /question
- `com.sourashis.quizapp.modules.quiz.controller.QuizController` — /quiz

Run & test locally (Windows PowerShell)

From the project root (where `mvnw.cmd` is located):

```powershell
# Run using Maven wrapper
.\mvnw.cmd spring-boot:run

# Or build and run jar
.\mvnw.cmd clean package
java -jar target\<your-artifact-name>.jar
```

Default server port: 8080 (unless overridden in `src/main/resources/application.properties`).

Authentication

- The application uses JWT-based tokens (see `AuthenticationResponse` which contains `authToken` and `refreshToken`).
- To call protected endpoints include the header:

  Authorization: Bearer <authToken>

- Role-based access is used with `@PreAuthorize`:
  - ADMIN-only endpoints: category and question management, quiz creation
  - ADMIN or USER: reading questions, taking/submitting quizzes

API endpoints

All responses are wrapped in an `ApiResponseWrapper<T>` (see "Response wrapper and error format" section).

1) Auth

Base path: /auth

- POST /auth/register?admin=false
  - Description: Register a new user. Set `admin=true` query param to create an admin user (protected usage; this code accepts the param but calling without appropriate privileges will vary by configuration).
  - Request Body: AuthenticationRequest
    {
      "username": "string",
      "password": "string",
      "email": "string",
      "phoneNumber": "string",
      "address": "string"
    }
  - Response (201 Created): ApiResponseWrapper<AuthenticationResponse>

- POST /auth/login
  - Description: Login with username and password
  - Request Body: AuthenticationRequest (login fields only)
    {
      "username": "string",
      "password": "string"
    }
  - Response (200 OK): ApiResponseWrapper<AuthenticationResponse>

- POST /auth/refresh
  - Description: Refresh access token using a refresh token
  - Request Body: RefreshTokenRequest
    {
      "refreshToken": "string"
    }
  - Response (200 OK): ApiResponseWrapper<AuthenticationResponse>

- POST /auth/logout
  - Description: Revoke a refresh token / logout
  - Request Body: RefreshTokenRequest
  - Response (200 OK): ApiResponseWrapper<Void>

Authentication DTOs (shapes)

- AuthenticationRequest
  - username: string
  - password: string
  - email: string (required on register)
  - phoneNumber: string (required on register)
  - address: string (required on register)

- AuthenticationResponse
  - username: string
  - role: string
  - email: string
  - phoneNumber: string
  - address: string
  - profilePicture: string (nullable)
  - refreshToken: string
  - authToken: string

- RefreshTokenRequest
  - refreshToken: string

2) Questions

Base path: /question

- GET /question/all
  - Description: Return all questions (no pagination)
  - Authorization: hasAnyRole('ADMIN','USER')
  - Response (200 OK): ApiResponseWrapper<List<QuestionResponse>>

- GET /question/all/paged?page=0&size=10&sortBy=id
  - Description: Return paginated questions with pagination metadata
  - Authorization: hasAnyRole('ADMIN','USER')
  - Response (200 OK): ApiResponseWrapper<List<QuestionResponse>> with `meta` (PaginationMeta)

- GET /question/category/{categoryName}
  - Description: Return questions filtered by category name
  - Authorization: hasAnyRole('ADMIN','USER')
  - Response (200 OK): ApiResponseWrapper<List<QuestionResponse>>

- GET /question/category-id/{categoryId}
  - Description: Return questions filtered by category id
  - Authorization: hasAnyRole('ADMIN','USER')
  - Response (200 OK): ApiResponseWrapper<List<QuestionResponse>>

- POST /question/add
  - Description: Create a new question (ADMIN only)
  - Authorization: hasRole('ADMIN')
  - Request Body: QuestionRequest
    {
      "questionTitle": "string",
      "option1": "string",
      "option2": "string",
      "option3": "string",
      "option4": "string",
      "rightAnswer": "string",
      "difficultyLevel": "string",
      "categoryId": 1
    }
  - Response (201 Created): ApiResponseWrapper<QuestionResponse>

- DELETE /question/delete/{id}
  - Description: Delete a question by id (ADMIN only)
  - Authorization: hasRole('ADMIN')
  - Response (200 OK): ApiResponseWrapper<QuestionResponse> (deleted resource)

Question DTOs (shapes)

- QuestionRequest
  - questionTitle: string
  - option1: string
  - option2: string
  - option3: string
  - option4: string
  - rightAnswer: string
  - difficultyLevel: string
  - categoryId: integer

- QuestionResponse
  - id: integer
  - questionTitle: string
  - option1: string
  - option2: string
  - option3: string
  - option4: string
  - difficultyLevel: string
  - categoryId: integer
  - categoryName: string

Notes: The `QuestionResponse` intentionally excludes `rightAnswer` to avoid exposing correct answers in read endpoints.

3) Quiz & Category

Base path: /quiz

- POST /quiz/create
  - Description: Create a new quiz by selecting random questions from a category (ADMIN only)
  - Authorization: hasRole('ADMIN')
  - Request Body: QuizRequest
    {
      "categoryId": 1,
      "numQuestions": 10,
      "title": "Sample Quiz"
    }
  - Response (201 Created): ApiResponseWrapper<QuizResponse>

- GET /quiz/{id}/questions
  - Description: Get quiz questions (rightAnswer excluded)
  - Authorization: hasAnyRole('ADMIN','USER')
  - Response (200 OK): ApiResponseWrapper<QuizResponse>

- POST /quiz/{id}/submit
  - Description: Submit answers for a quiz; returns score breakdown
  - Authorization: hasAnyRole('ADMIN','USER')
  - Request Body: List of SubmitAnswerRequest
    [
      { "id": 123, "response": "option1" },
      { "id": 124, "response": "option3" }
    ]
  - Response (200 OK): ApiResponseWrapper<QuizScoreResponse>

- GET /quiz/categories?page=0&size=10&sortBy=id
  - Description: Return paginated categories
  - Authorization: hasAnyRole('ADMIN','USER')
  - Response (200 OK): ApiResponseWrapper<List<CategoryResponse>> with PaginationMeta

- POST /quiz/category/add
  - Description: Add a new category (ADMIN only)
  - Authorization: hasRole('ADMIN')
  - Request Body: CategoryRequest
    { "categoryName": "Java" }
  - Response (201 Created): ApiResponseWrapper<CategoryResponse>

- PUT /quiz/category/edit/{id}
  - Description: Edit an existing category (ADMIN only)
  - Authorization: hasRole('ADMIN')
  - Request Body: CategoryRequest
  - Response (200 OK): ApiResponseWrapper<CategoryResponse>

- DELETE /quiz/category/delete/{id}
  - Description: Delete category (ADMIN only)
  - Authorization: hasRole('ADMIN')
  - Response (200 OK): ApiResponseWrapper<CategoryResponse>

Quiz & Category DTOs (shapes)

- QuizRequest
  - categoryId: integer
  - numQuestions: integer
  - title: string

- QuizResponse
  - id: integer
  - title: string
  - questions: array of QuizQuestionResponse

- QuizQuestionResponse
  - id: integer
  - questionTitle: string
  - option1: string
  - option2: string
  - option3: string
  - option4: string
  (rightAnswer is intentionally excluded)

- SubmitAnswerRequest
  - id: integer (question id)
  - response: string (user selected answer)

- QuizScoreResponse
  - quizId: integer
  - quizTitle: string
  - totalQuestions: integer
  - correctAnswers: integer
  - wrongAnswers: integer
  - scorePercentage: double

- CategoryRequest
  - categoryName: string

- CategoryResponse
  - id: integer
  - categoryName: string

Response wrapper and error format

All responses follow the common wrapper:

ApiResponseWrapper<T> (JSON fields)
- statusCode: integer
- message: string
- response: T (the actual payload; may be null)
- meta: PaginationMeta (optional — present only for paginated endpoints)

PaginationMeta fields
- currentPage: integer
- pageSize: integer
- totalElements: long
- totalPages: integer
- hasNext: boolean
- hasPrevious: boolean

Error responses

Errors are handled centrally (see `GlobalExceptionHandler`) and return an `ApiResponseWrapper<Void>` with the following shape:

Example (validation error / bad request):
{
  "statusCode": 400,
  "message": "Validation failed: field: message, ...",
  "response": null
}

Example (access denied):
{
  "statusCode": 403,
  "message": "Access Denied - You do not have the required permissions to access this resource. ADMIN role required.",
  "response": null
}

Examples

1) Login (curl)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "username":"admin", "password":"adminpass" }'
```

Example response (200 OK):

{
  "statusCode": 200,
  "message": "Login successful!",
  "response": {
    "username": "admin",
    "role": "ADMIN",
    "email": "admin@example.com",
    "phoneNumber": "1234567890",
    "address": "Some address",
    "profilePicture": null,
    "refreshToken": "<refresh-token>",
    "authToken": "<jwt-access-token>"
  }
}

2) Get all questions (paged) (curl)

```bash
curl -X GET "http://localhost:8080/question/all/paged?page=0&size=10&sortBy=id" \
  -H "Authorization: Bearer <authToken>"
```

Example response (200 OK):

{
  "statusCode": 200,
  "message": "Questions fetched successfully!",
  "response": [
    {
      "id": 1,
      "questionTitle": "Which keyword is used to inherit a class in Java?",
      "option1": "implements",
      "option2": "extends",
      "option3": "super",
      "option4": "this",
      "difficultyLevel": "EASY",
      "categoryId": 1,
      "categoryName": "Java"
    }
  ],
  "meta": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}

3) Create a question (ADMIN)

```bash
curl -X POST http://localhost:8080/question/add \
  -H "Authorization: Bearer <adminAuthToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "questionTitle": "Which keyword is used to inherit a class in Java?",
    "option1": "implements",
    "option2": "extends",
    "option3": "super",
    "option4": "this",
    "rightAnswer": "extends",
    "difficultyLevel": "EASY",
    "categoryId": 1
  }'
```

Example response (201 Created):

{
  "statusCode": 201,
  "message": "Question added successfully!",
  "response": {
    "id": 2,
    "questionTitle": "Which keyword is used to inherit a class in Java?",
    "option1": "implements",
    "option2": "extends",
    "option3": "super",
    "option4": "this",
    "difficultyLevel": "EASY",
    "categoryId": 1,
    "categoryName": "Java"
  }
}

Notes & next steps

- This README reflects the controllers and DTOs currently present in the codebase. If you add more endpoints, update the README similarly.
- If you want, I can also:
  - Add Postman collection / examples for every endpoint
  - Seed the database with `data.sql` sample questions and categories
  - Add swagger / OpenAPI documentation (Springdoc) and a generated API docs page

---

If you'd like any change to the README formatting or to include additional example responses, tell me which endpoints you'd like more examples for and I will update the file.
