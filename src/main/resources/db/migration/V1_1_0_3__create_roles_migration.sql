-- Drop existing tables if they exist
DROP TABLE IF EXISTS users_roles CASCADE;
DROP TABLE IF EXISTS users_roles_given CASCADE;

-- Create the users_roles table
CREATE TABLE users_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create the users_roles_given table
CREATE TABLE users_roles_given (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);