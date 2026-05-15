CREATE TABLE basic_questions (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    created_by BINARY(16) NOT NULL,
    question_text TEXT NOT NULL,
    answer_text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_basic_questions_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE basic_question_attempts (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    question_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    submitted_answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_basic_question_attempts_question_id FOREIGN KEY (question_id) REFERENCES basic_questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_basic_question_attempts_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_basic_questions_created_by ON basic_questions (created_by);
CREATE INDEX idx_basic_question_attempts_question_id ON basic_question_attempts (question_id);
CREATE INDEX idx_basic_question_attempts_user_id ON basic_question_attempts (user_id);
