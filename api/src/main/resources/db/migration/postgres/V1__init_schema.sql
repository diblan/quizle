-- V1__init_schema.sql

-- Enable useful extensions (safe to run multiple times)
CREATE EXTENSION IF NOT EXISTS citext;     -- case-insensitive text (emails/usernames)

-- ============================================
-- USERS
-- ============================================
CREATE TABLE users (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username     CITEXT UNIQUE NOT NULL,
  email        CITEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  -- optional profile fields...
  CHECK (length(username) >= 3)
);
