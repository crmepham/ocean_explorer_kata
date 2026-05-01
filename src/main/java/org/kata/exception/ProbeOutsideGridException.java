package org.kata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ProbeOutsideGridException extends RuntimeException {

    public ProbeOutsideGridException(int x, int y) {
        super("Position (" + x + ", " + y + ") is outside the grid bounds");
    }
}
