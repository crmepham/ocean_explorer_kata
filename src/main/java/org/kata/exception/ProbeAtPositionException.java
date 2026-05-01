package org.kata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProbeAtPositionException extends RuntimeException {

    public ProbeAtPositionException(int x, int y) {
        super("A probe is already occupying position (" + x + ", " + y + ")");
    }
}
