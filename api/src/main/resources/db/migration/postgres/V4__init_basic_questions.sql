ALTER TABLE users
  ADD COLUMN role TEXT NOT NULL DEFAULT 'USER';

CREATE TABLE basic_questions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  question_text TEXT NOT NULL,
  answer_text TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE basic_question_attempts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  question_id UUID NOT NULL REFERENCES basic_questions(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  submitted_answer TEXT NOT NULL,
  is_correct BOOLEAN NOT NULL,
  attempted_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_basic_questions_created_by ON basic_questions (created_by);
CREATE INDEX idx_basic_question_attempts_question_id ON basic_question_attempts (question_id);
CREATE INDEX idx_basic_question_attempts_user_id ON basic_question_attempts (user_id);
