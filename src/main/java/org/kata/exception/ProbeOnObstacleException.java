package org.kata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProbeOnObstacleException extends RuntimeException {

    public ProbeOnObstacleException(int x, int y) {
        super("Position (" + x + ", " + y + ") is occupied by an obstacle");
    }
}
