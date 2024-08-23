CREATE SCHEMA IF NOT EXISTS virtual_pet_schema;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE virtual_pet_schema.thread (
    id SERIAL PRIMARY KEY,
    thread_id UUID,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_id ON virtual_pet_schema.thread (user_id);
CREATE INDEX IF NOT EXISTS idx_thread_id ON virtual_pet_schema.thread (thread_id);
