CREATE TABLE question_sets (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    created_by BINARY(16) NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_sets_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE question_set_items (
    id BINARY(16) PRIMARY KEY DEFAULT (uuid_v4()),
    question_set_id BINARY(16) NOT NULL,
    position INT NOT NULL CHECK (position > 0),
    question_type VARCHAR(50) NOT NULL CHECK (question_type IN ('BASIC', 'SET_ANSWER')),
    basic_question_id BINARY(16),
    set_answer_question_id BINARY(16),
    CONSTRAINT fk_question_set_items_question_set_id FOREIGN KEY (question_set_id) REFERENCES question_sets(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_set_items_basic_question_id FOREIGN KEY (basic_question_id) REFERENCES basic_questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_set_items_set_answer_question_id FOREIGN KEY (set_answer_question_id) REFERENCES set_answer_questions(id) ON DELETE CASCADE,
    CONSTRAINT uq_question_set_items_position UNIQUE (question_set_id, position),
    CONSTRAINT chk_question_set_items_question_reference CHECK (
        (question_type = 'BASIC' AND basic_question_id IS NOT NULL AND set_answer_question_id IS NULL)
        OR
        (question_type = 'SET_ANSWER' AND set_answer_question_id IS NOT NULL AND basic_question_id IS NULL)
    )
);

CREATE INDEX idx_question_sets_created_by ON question_sets (created_by);
CREATE INDEX idx_question_set_items_question_set_id ON question_set_items (question_set_id);
CREATE INDEX idx_question_set_items_basic_question_id ON question_set_items (basic_question_id);
CREATE INDEX idx_question_set_items_set_answer_question_id ON question_set_items (set_answer_question_id);
