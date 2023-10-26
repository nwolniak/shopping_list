-- Items schema

-- !Ups
CREATE TABLE shopping_lists
(
    id SERIAL PRIMARY KEY
);

CREATE TABLE Items
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    shopping_list_id BIGINT       NOT NULL,
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists (id)
);

-- ALTER TABLE Items
--     ADD CONSTRAINT fk_shopping_list
--         FOREIGN KEY (shopping_list_id)
--             REFERENCES shopping_lists (id);

-- !Downs
DROP TABLE Items;
DROP TABLE shopping_lists;

--
-- ALTER TABLE Items
--     DROP CONSTRAINT fk_shopping_list;