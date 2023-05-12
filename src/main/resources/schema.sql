DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;

CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
user_name VARCHAR(255) NOT NULL,
email VARCHAR(512) NOT NULL,
CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
description VARCHAR(512) NOT NULL,
requester_id BIGINT REFERENCES users (id),
created_date TIMESTAMP WITH TIME ZONE NOT NULL,
CONSTRAINT PK_REQUESTS PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
item_name VARCHAR(255) NOT NULL,
description VARCHAR(512),
is_available BOOLEAN NOT NULL,
owner_id BIGINT REFERENCES users (id),
request_id BIGINT REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
item_id INTEGER REFERENCES items (id),
booker_id INTEGER REFERENCES users (id),
status VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
text VARCHAR(512) NOT NULL,
item_id INTEGER REFERENCES items (id),
author_id INTEGER REFERENCES users (id)
);
