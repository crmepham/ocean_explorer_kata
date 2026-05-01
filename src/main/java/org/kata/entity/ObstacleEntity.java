package org.kata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "obstacle")
public class ObstacleEntity extends BaseEntity {

    @Column(name = "grid_id", nullable = false)
    private Long gridId;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    protected ObstacleEntity() {}

    public ObstacleEntity(Long gridId, int x, int y) {
        this.gridId = gridId;
        this.x = x;
        this.y = y;
    }

    public Long getGridId() { return gridId; }
    public int getX() { return x; }
    public int getY() { return y; }
}
