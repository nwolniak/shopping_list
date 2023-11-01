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
    id SERIAL PRIMARY KEY
);

CREATE TABLE Items
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    shopping_list_id BIGINT       NOT NULL,
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists (id) ON DELETE CASCADE
);

-- ALTER TABLE Items
--     ADD CONSTRAINT fk_shopping_list
--         FOREIGN KEY (shopping_list_id)
--             REFERENCES shopping_lists (id);

-- !Downs
DROP TABLE users;
DROP TABLE Items;
DROP TABLE shopping_lists CASCADE;

--
-- ALTER TABLE Items
--     DROP CONSTRAINT fk_shopping_list;