CREATE TABLE accounts
(
    id         SERIAL PRIMARY KEY,
    first_name varchar(20),
    last_name  varchar(20),
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL
);