CREATE TABLE obstacle (
    id      BIGINT GENERATED ALWAYS AS IDENTITY,
    grid_id BIGINT NOT NULL,
    x       INT    NOT NULL,
    y       INT    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_obstacle_grid FOREIGN KEY (grid_id) REFERENCES grid (id)
);
