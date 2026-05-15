CREATE TABLE set_answer_questions (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    created_by BINARY(16) NOT NULL,
    question_text TEXT NOT NULL,
    required_answers INT NOT NULL CHECK (required_answers > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_set_answer_questions_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE set_answer_question_solutions (
    question_id BINARY(16) NOT NULL,
    solution_order INT NOT NULL,
    answer_text TEXT NOT NULL,
    PRIMARY KEY (question_id, solution_order),
    CONSTRAINT fk_set_answer_question_solutions_question_id FOREIGN KEY (question_id) REFERENCES set_answer_questions(id) ON DELETE CASCADE
);

CREATE TABLE set_answer_question_attempts (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    question_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    correct_answers INT NOT NULL DEFAULT 0,
    is_correct BOOLEAN NOT NULL,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_set_answer_question_attempts_question_id FOREIGN KEY (question_id) REFERENCES set_answer_questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_set_answer_question_attempts_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE set_answer_question_attempt_answers (
    attempt_id BINARY(16) NOT NULL,
    answer_order INT NOT NULL,
    answer_text TEXT NOT NULL,
    PRIMARY KEY (attempt_id, answer_order),
    CONSTRAINT fk_set_answer_question_attempt_answers_attempt_id FOREIGN KEY (attempt_id) REFERENCES set_answer_question_attempts(id) ON DELETE CASCADE
);

CREATE INDEX idx_set_answer_questions_created_by ON set_answer_questions (created_by);
CREATE INDEX idx_set_answer_question_attempts_question_id ON set_answer_question_attempts (question_id);
CREATE INDEX idx_set_answer_question_attempts_user_id ON set_answer_question_attempts (user_id);
