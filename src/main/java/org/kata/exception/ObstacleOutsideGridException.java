package org.kata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ObstacleOutsideGridException extends RuntimeException {

    public ObstacleOutsideGridException(int x, int y) {
        super("Position (" + x + ", " + y + ") is outside the grid bounds");
    }
}
