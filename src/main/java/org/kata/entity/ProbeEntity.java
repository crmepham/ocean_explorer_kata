package org.kata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.kata.domain.Direction;

@Entity
@Table(name = "probe")
public class ProbeEntity extends BaseEntity {

    @Column(name = "grid_id", nullable = false)
    private Long gridId;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    protected ProbeEntity() {}

    public ProbeEntity(Long gridId, int x, int y, Direction direction) {
        this.gridId = gridId;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void updatePosition(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public Long getGridId() { return gridId; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return direction; }
}
