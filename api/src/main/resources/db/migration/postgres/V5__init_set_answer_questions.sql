CREATE TABLE set_answer_questions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  question_text TEXT NOT NULL,
  required_answers INT NOT NULL CHECK (required_answers > 0),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE set_answer_question_solutions (
  question_id UUID NOT NULL REFERENCES set_answer_questions(id) ON DELETE CASCADE,
  solution_order INT NOT NULL,
  answer_text TEXT NOT NULL,
  PRIMARY KEY (question_id, solution_order)
);

CREATE TABLE set_answer_question_attempts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  question_id UUID NOT NULL REFERENCES set_answer_questions(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  correct_answers INT NOT NULL DEFAULT 0,
  is_correct BOOLEAN NOT NULL,
  attempted_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE set_answer_question_attempt_answers (
  attempt_id UUID NOT NULL REFERENCES set_answer_question_attempts(id) ON DELETE CASCADE,
  answer_order INT NOT NULL,
  answer_text TEXT NOT NULL,
  PRIMARY KEY (attempt_id, answer_order)
);

CREATE INDEX idx_set_answer_questions_created_by ON set_answer_questions (created_by);
CREATE INDEX idx_set_answer_question_attempts_question_id ON set_answer_question_attempts (question_id);
CREATE INDEX idx_set_answer_question_attempts_user_id ON set_answer_question_attempts (user_id);
