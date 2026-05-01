package org.kata.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "grid")
public class GridEntity extends BaseEntity {

    private int width;
    private int height;

    protected GridEntity() {}

    public GridEntity(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void updateDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
