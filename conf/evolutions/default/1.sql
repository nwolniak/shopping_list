-- Items schema

-- !Ups
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE shopping_lists
(
    id            SERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    purchase_date DATE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE items
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    units            BIGINT       NOT NULL,
    unit_type        VARCHAR(255) NOT NULL,
    is_realized      BOOLEAN      NOT NULL,
    shopping_list_id BIGINT       NOT NULL,
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists (id) ON DELETE CASCADE
);

-- !Downs
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS shopping_lists;
DROP TABLE IF EXISTS users;
