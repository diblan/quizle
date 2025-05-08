-- V1__init_schema.sql

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL, -- USER, ADMIN
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE quizzes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_quizzes_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quiz_id UUID NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL, -- SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_questions_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

CREATE TABLE answer_options (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    question_id UUID NOT NULL,
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_answer_options_question_id FOREIGN KEY (question_id) REFERENCES questions(id)
);

CREATE TABLE user_answers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    quiz_id UUID NOT NULL,
    question_id UUID NOT NULL,
    answer_option_id UUID, -- Nullable if text answer
    text_answer TEXT,
    answered_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_answers_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_answers_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes(id),
    CONSTRAINT fk_user_answers_question_id FOREIGN KEY (question_id) REFERENCES questions(id),
    CONSTRAINT fk_user_answers_answer_option_id FOREIGN KEY (answer_option_id) REFERENCES answer_options(id)
);

CREATE TABLE quiz_results (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    quiz_id UUID NOT NULL,
    score DECIMAL(5,2) NOT NULL, -- Example: 85.50
    completed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_quiz_results_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_quiz_results_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- Optional: create indexes for faster lookups
CREATE INDEX idx_quizzes_created_by ON quizzes (created_by);
CREATE INDEX idx_questions_quiz_id ON questions (quiz_id);
CREATE INDEX idx_answer_options_question_id ON answer_options (question_id);
CREATE INDEX idx_user_answers_user_id ON user_answers (user_id);
CREATE INDEX idx_user_answers_quiz_id ON user_answers (quiz_id);
CREATE INDEX idx_quiz_results_user_id ON quiz_results (user_id);
CREATE INDEX idx_quiz_results_quiz_id ON quiz_results (quiz_id);
