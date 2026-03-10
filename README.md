# QuizApp

A small Spring Boot application that manages quiz questions (CRUD) and serves them over a REST API.

## Description

This project provides a simple RESTful backend for storing multiple-choice questions. It includes an entity for `Question` (fields typically include: `questionTitle`, `option1`..`option4`, `rightAnswer`, `difficultyLevel`, and `category`) and a controller/service/DAO layer to perform CRUD operations.

> Note: This README is written from the repository structure and typical conventions; if your code uses different field or endpoint names, adapt the examples below.

## Tech stack

- Java (Spring Boot)
- Maven (wrapper present: `mvnw` / `mvnw.cmd`)
- JPA / Hibernate (repository layer)
- Any configured relational database (H2, MySQL, PostgreSQL, etc.) via `src/main/resources/application.properties`

## Requirements

- JDK 11+ (or the version your project uses)
- Git (optional)
- Maven (not required when using the included wrapper)

## Run (Windows PowerShell)

Start the application using the included Maven wrapper:

```powershell
# from project root (where mvnw.cmd is located)
.\mvnw.cmd spring-boot:run
```

Or build a jar and run it:

```powershell
.\mvnw.cmd clean package
java -jar target\your-artifact-name.jar
```

## Typical REST endpoints (assumed)

The controller usually exposes endpoints similar to these:

- GET  /questions           — list all questions
- GET  /questions/{id}      — fetch a question by id
- POST /questions           — create a new question
- PUT  /questions/{id}      — update an existing question
- DELETE /questions/{id}    — delete a question

If your controller uses a different base path, substitute it accordingly.

## Example JSON (create a question)

Use this JSON payload with POST `/questions` (replace host/port if different):

```json
{
  "category": "Java",
  "difficultyLevel": "EASY",
  "option1": "implements",
  "option2": "extends",
  "option3": "super",
  "option4": "this",
  "questionTitle": "Which keyword is used to inherit a class in Java?",
  "rightAnswer": "extends"
}
```

Curl example:

```bash
curl -X POST http://localhost:8080/questions \
  -H "Content-Type: application/json" \
  -d '{ "category": "Java", "difficultyLevel": "EASY", "option1": "implements", "option2": "extends", "option3": "super", "option4": "this", "questionTitle": "Which keyword is used to inherit a class in Java?", "rightAnswer": "extends" }'
```

(Windows PowerShell users may prefer to use a file or an HTTP client like Postman.)

## SQL example (insert directly into `question` table)

If you prefer to seed the database with SQL (adjust column names to match your schema):

```sql
INSERT INTO question (question_title, option1, option2, option3, option4, right_answer, difficulty_level, category) VALUES
('Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'super', 'this', 'extends', 'EASY', 'Java');
```

You may place such statements in `src/main/resources/data.sql` to have Spring Boot execute them at startup (when using an embedded database or `spring.datasource.initialize` behavior).

## Tests

Run unit/integration tests with:

```powershell
.\mvnw.cmd test
```

## Configuration

Open `src/main/resources/application.properties` to configure your datasource (JDBC URL, username/password, dialect). If you want an in-memory demo, you can use H2.

## Notes & assumptions

- Assumed entity property names and REST paths are based on the project structure. If the code uses different field names (e.g., `question_title` vs `questionTitle`) or controller paths, update the examples accordingly.
- If you'd like, I can automatically add a sample `data.sql` with demo questions, or update the README with exact endpoints after you share the controller file.

---

If you want, I can now:

- Add a `data.sql` containing the demo questions we discussed.
- Generate curl/Postman examples for all endpoints.
- Update the README with the exact endpoints (if you let me read `QuestionController.java`).

Tell me which of the above you'd like next.
