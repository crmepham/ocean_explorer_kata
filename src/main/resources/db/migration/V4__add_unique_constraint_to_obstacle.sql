ALTER TABLE obstacle
    ADD CONSTRAINT uq_obstacle_grid_position UNIQUE (grid_id, x, y);
