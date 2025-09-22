-- -----------------------
-- QUIZ CORE
-- -----------------------

CREATE TABLE quizzes (
  id          BIGSERIAL PRIMARY KEY,
  owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title       TEXT NOT NULL,
  description TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Common parent for all questions
CREATE TABLE questions (
  id            BIGSERIAL PRIMARY KEY,
  quiz_id       BIGINT NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
  prompt        TEXT NOT NULL,
  question_type TEXT NOT NULL CHECK (question_type IN
    ('SINGLE_ANSWER','MULTIPLE_CHOICE','LIST','MATCH')
  ),
  position      INT NOT NULL, -- ordering inside quiz
  UNIQUE (quiz_id, position)
);

-- -----------------------
-- SINGLE ANSWER
-- -----------------------

-- Single expected answer (exact or normalized match)
CREATE TABLE single_answer_questions (
  question_id BIGINT PRIMARY KEY REFERENCES questions(id) ON DELETE CASCADE,
  expected_answer TEXT NOT NULL
);

-- -----------------------
-- MULTIPLE CHOICE
-- -----------------------

-- MCQ subtype table: captures selection rules
CREATE TABLE multiple_choice_questions (
  question_id BIGINT PRIMARY KEY REFERENCES questions(id) ON DELETE CASCADE,
  min_correct INT NOT NULL DEFAULT 1,
  max_correct INT NOT NULL DEFAULT 1
);

-- Options for MCQ questions
CREATE TABLE multiple_choice_options (
  id          BIGSERIAL PRIMARY KEY,
  question_id BIGINT NOT NULL REFERENCES question_multiple_choice(question_id) ON DELETE CASCADE,
  option_text TEXT NOT NULL,
  is_correct  BOOLEAN NOT NULL DEFAULT FALSE
);

-- -----------------------
-- COMPLETE LIST
-- -----------------------

-- Multiple answers are required to complete the list (e.g. list me the states of matter)
CREATE TABLE complete_list_questions (
  question_id BIGINT PRIMARY KEY REFERENCES questions(id) ON DELETE CASCADE,
  min_required INT NOT NULL DEFAULT 1
);

-- Expected answers for list questions
CREATE TABLE list_expected_terms (
  id          BIGSERIAL PRIMARY KEY,
  question_id BIGINT NOT NULL REFERENCES complete_list_questions(question_id) ON DELETE CASCADE,
  canonical   TEXT NOT NULL,
  UNIQUE (question_id, canonical)
);

-- -----------------------
-- MATCH
-- -----------------------

-- Match the right items with the left items
CREATE TABLE match_questions (
  question_id BIGINT PRIMARY KEY REFERENCES questions(id) ON DELETE CASCADE
);

-- Left side items
CREATE TABLE match_left (
  id          BIGSERIAL PRIMARY KEY,
  question_id BIGINT NOT NULL REFERENCES match_questions(question_id) ON DELETE CASCADE,
  item_text   TEXT NOT NULL
);

-- Right side items
CREATE TABLE match_right (
  id          BIGSERIAL PRIMARY KEY,
  question_id BIGINT NOT NULL REFERENCES match_questions(question_id) ON DELETE CASCADE,
  item_text   TEXT NOT NULL
);

-- The correct mapping (left → right)
CREATE TABLE match_pairs (
  left_id BIGINT NOT NULL REFERENCES match_left(id) ON DELETE CASCADE,
  right_id BIGINT NOT NULL REFERENCES match_right(id) ON DELETE CASCADE,
  PRIMARY KEY (left_id, right_id)
);
