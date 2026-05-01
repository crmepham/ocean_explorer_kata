package org.kata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GridNotFoundException extends RuntimeException {

    public GridNotFoundException(Long id) {
        super("Grid not found with id: " + id);
    }
}
