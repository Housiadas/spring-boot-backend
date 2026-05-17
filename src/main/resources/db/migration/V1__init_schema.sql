CREATE TABLE users (
    id          uuid         PRIMARY KEY,
    first_name  varchar(255) NOT NULL,
    last_name   varchar(255) NOT NULL,
    email       varchar(100) NOT NULL UNIQUE,
    password    varchar(255) NOT NULL,
    created_at  timestamp(6) NOT NULL,
    updated_at  timestamp(6)
);

CREATE TABLE user_authorities (
    user_id    uuid         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    authority  varchar(255)
);
CREATE INDEX idx_user_authorities_user_id ON user_authorities (user_id);

CREATE TABLE todos (
    id           uuid         PRIMARY KEY,
    title        varchar(255) NOT NULL,
    description  varchar(255) NOT NULL,
    priority     integer      NOT NULL,
    complete     boolean      NOT NULL,
    owner_id     uuid         NOT NULL REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_todos_owner_id ON todos (owner_id);
