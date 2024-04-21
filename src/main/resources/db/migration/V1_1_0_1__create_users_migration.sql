DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
   id BIGSERIAL PRIMARY KEY,
   email VARCHAR(100) UNIQUE NOT NULL,
   username VARCHAR(100) NOT NULL,
   password VARCHAR(100) NOT NULL
);