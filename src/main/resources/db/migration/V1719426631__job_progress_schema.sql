CREATE SCHEMA IF NOT EXISTS virtual_pet_schema;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS virtual_pet_schema.uploaded_original_photos (
    photo_id UUID DEFAULT uuid_generate_v4() PRIMARY KEY, -- Primary key, auto-generated UUID.
    user_id UUID NOT NULL, -- user_id linking to the users table
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp of when the photo was uploaded.
    metadata JSONB NOT NULL, -- Metadata of the photo, stored in JSONB format.
    s3_key VARCHAR(255) NOT NULL  -- S3 storage key for the photo, cannot be null.
    );
-- Create index on 'photo_id' for faster queries
CREATE INDEX IF NOT EXISTS uploaded_original_photos_photo_id_idx ON virtual_pet_schema.uploaded_original_photos (photo_id);

CREATE TABLE IF NOT EXISTS virtual_pet_schema.photo_enhancement_jobs (
    job_id UUID DEFAULT uuid_generate_v4() PRIMARY KEY, -- Primary key, auto-generated UUID.
    user_id UUID NOT NULL, -- user_id linking to the users table
    photo_id UUID NOT NULL, --linking to the uploaded_original_photos table
    job_type VARCHAR(50) NOT NULL, -- Type of the job : 'enhance', 'stylize', 'composite'.
    job_submission_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Timestamp of when the job was submitted.
    );
CREATE INDEX photo_enhancement_jobs_job_id_idx ON virtual_pet_schema.photo_enhancement_jobs (job_id);

CREATE TABLE IF NOT EXISTS virtual_pet_schema.photo_enhanced_results (
    result_id UUID DEFAULT uuid_generate_v4() PRIMARY KEY, -- Primary key, auto-generated UUID.
    user_id UUID NOT NULL, -- user_id linking to the users table
    job_id UUID NOT NULL, --linking to the photo_enhancement_jobs table
    generated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp of when the result was generated.
    s3_key VARCHAR(255) NOT NULL -- S3 storage key for the result, cannot be null
    );
-- Create index on 'result_id' for faster queries
CREATE INDEX photo_enhanced_results_result_id_idx ON virtual_pet_schema.photo_enhanced_results (result_id);