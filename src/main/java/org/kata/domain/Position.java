package org.kata.domain;

public interface Position {
    Position translate(MovementVector vector);
}
