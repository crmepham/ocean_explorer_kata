CREATE TABLE grid (
    id     BIGINT       GENERATED ALWAYS AS IDENTITY,
    width  INT          NOT NULL,
    height INT          NOT NULL,
    PRIMARY KEY (id)
);
