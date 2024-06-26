CREATE SCHEMA IF NOT EXISTS virtual_pet_schema;
CREATE TABLE virtual_pet_schema.thread (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    thread_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_id ON virtual_pet_schema.thread (user_id);
CREATE INDEX IF NOT EXISTS idx_thread_id ON virtual_pet_schema.thread (thread_id);

