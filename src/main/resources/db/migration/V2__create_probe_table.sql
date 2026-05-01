CREATE TABLE probe (
    id        BIGINT      NOT NULL AUTO_INCREMENT,
    grid_id   BIGINT      NOT NULL,
    x         INT         NOT NULL,
    y         INT         NOT NULL,
    direction VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_probe_grid FOREIGN KEY (grid_id) REFERENCES grid (id)
);
