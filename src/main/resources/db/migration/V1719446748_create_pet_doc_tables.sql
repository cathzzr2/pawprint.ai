CREATE SCHEMA IF NOT EXISTS virtual_pet_schema;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE IF NOT EXISTS virtual_pet_schema.pets (
    pet_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    pet_name VARCHAR(50) NOT NULL,
    pet_type VARCHAR(50) NOT NULL,
    pet_breed VARCHAR(50) NOT NULL,
    pet_age SMALLINT NOT NULL,
    pet_birthdate DATE NOT NULL,
    pet_gender VARCHAR(10) NOT NULL,
    pet_color VARCHAR(50) NOT NULL,
    owner_id UUID UNIQUE NOT NULL
    );

CREATE INDEX IF NOT EXISTS users_pet_name_idx ON virtual_pet_schema.pets (pet_name);

CREATE TABLE IF NOT EXISTS virtual_pet_schema.users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_name VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    last_active TIMESTAMP DEFAULT current_timestamp
    );

CREATE TABLE IF NOT EXISTS virtual_pet_schema.user_pet (
    user_id UUID NOT NULL,
    pet_id UUID NOT NULL
    );
