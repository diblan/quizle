-- UUIDv4 generator function (MariaDB workaround)
DELIMITER //

CREATE FUNCTION uuid_v4()
RETURNS BINARY(16)
DETERMINISTIC
BEGIN
    SET @rnd = UNHEX(REPLACE(UUID(), '-', ''));
    -- Set version to 4
    SET @rnd = INSERT(@rnd, 7, 1, CHAR(ASCII(SUBSTRING(@rnd, 7, 1)) & 0x0f | 0x40));
    -- Set variant to 10xxxxxx
    SET @rnd = INSERT(@rnd, 9, 1, CHAR(ASCII(SUBSTRING(@rnd, 9, 1)) & 0x3f | 0x80));
    RETURN @rnd;
END //

DELIMITER ;
-- V1__init_schema.sql for MariaDB
-- Table: users
CREATE TABLE users (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL, -- USER, ADMIN
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: quizzes
CREATE TABLE quizzes (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by BINARY(16) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quizzes_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Table: questions
CREATE TABLE questions (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    quiz_id BINARY(16) NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL, -- SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- Table: answer_options
CREATE TABLE answer_options (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    question_id BINARY(16) NOT NULL,
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_answer_options_question_id FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- Table: user_answers
CREATE TABLE user_answers (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    user_id BINARY(16) NOT NULL,
    quiz_id BINARY(16) NOT NULL,
    question_id BINARY(16) NOT NULL,
    answer_option_id BINARY(16), -- Nullable if text answer
    text_answer TEXT,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_answers_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_answers_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes(id),
    CONSTRAINT fk_user_answers_question_id FOREIGN KEY (question_id) REFERENCES questions(id),
    CONSTRAINT fk_user_answers_answer_option_id FOREIGN KEY (answer_option_id) REFERENCES answer_options(id)
);

-- Table: quiz_results
CREATE TABLE quiz_results (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    user_id BINARY(16) NOT NULL,
    quiz_id BINARY(16) NOT NULL,
    score DECIMAL(5,2) NOT NULL, -- Example: 85.50
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_results_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_quiz_results_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- Indexes
CREATE INDEX idx_quizzes_created_by ON quizzes (created_by);
CREATE INDEX idx_questions_quiz_id ON questions (quiz_id);
CREATE INDEX idx_answer_options_question_id ON answer_options (question_id);
CREATE INDEX idx_user_answers_user_id ON user_answers (user_id);
CREATE INDEX idx_user_answers_quiz_id ON user_answers (quiz_id);
CREATE INDEX idx_quiz_results_user_id ON quiz_results (user_id);
CREATE INDEX idx_quiz_results_quiz_id ON quiz_results (quiz_id);
