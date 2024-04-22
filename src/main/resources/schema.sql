CREATE SEQUENCE IF NOT EXISTS user_id_seq;
CREATE SEQUENCE IF NOT EXISTS item_id_seq;
CREATE SEQUENCE IF NOT EXISTS booking_id_seq;
CREATE SEQUENCE IF NOT EXISTS comment_id_seq;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT       NOT NULL DEFAULT nextval('user_id_seq') PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT                       NOT NULL DEFAULT nextval('item_id_seq') PRIMARY KEY,
    name         VARCHAR(255)                 NOT NULL,
    description  VARCHAR(512)                 NOT NULL,
    is_available BOOLEAN                               DEFAULT FALSE,
    owner_id     BIGINT REFERENCES users (id) NOT NULL,
    request      BIGINT
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT                       NOT NULL DEFAULT nextval('booking_id_seq') PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
    item_id    BIGINT REFERENCES items (id) NOT NULL,
    booker_id  BIGINT REFERENCES users (id) NOT NULL,
    status     VARCHAR(255)                 NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT                       NOT NULL DEFAULT nextval('comment_id_seq') PRIMARY KEY,
    text         VARCHAR(512)                 NOT NULL,
    item_id      BIGINT REFERENCES items (id) NOT NULL,
    author_id    BIGINT REFERENCES users (id) NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE  NOT NULL
);