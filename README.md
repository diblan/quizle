# Quizle

Quizle is a self-hosted quiz platform inspired by daily puzzle games like **Wordle** and **Loldle**. The goal is to let you create your own quizzes and practice them each day in a fun way.

## Features

* **User registration** – register new players using a simple form.
* **Question practice APIs** – create and answer basic questions and fixed-size set-answer questions such as “Name the layers of the OSI model.”
* **Question set APIs** – create quiz-style sets that combine multiple ordered questions, such as a basic OSI layer-number question followed by a complete-list OSI layers question.
* **Planned:** track quiz-style set results across multiple questions.

The database schema includes tables for standalone questions, question sets and existing quiz/answer groundwork for future attempt tracking.

## Technologies Used

* Java 24
* Spring Boot (web, security, data JPA, Thymeleaf)
* Jakarta Faces (JSF) via JoinFaces
* Flyway for database migrations
* Maven for the build
* Tested with JUnit and Spring Boot test utilities

The project provides sample Flyway migrations for PostgreSQL, CockroachDB and MariaDB.

## Running Locally

1. Install **Java 24** and **Maven** (or use the provided Maven Wrapper).
2. Configure environment variables for your database. For example using PostgreSQL:
   ```bash
   export POSTGRES_URL=jdbc:postgresql://localhost:5432/quizle
   export POSTGRES_USER=quizle
   export POSTGRES_PASSWORD=secret
   ```
3. Start the application with
   ```bash
   ./mvnw spring-boot:run
   ```
   or `mvn spring-boot:run` if Maven is installed globally.
4. Visit `http://localhost:8080/register` to access the registration page.

## License

This project is licensed under the [Apache 2.0](LICENSE) license.
