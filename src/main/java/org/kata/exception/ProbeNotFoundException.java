package org.kata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProbeNotFoundException extends RuntimeException {

    public ProbeNotFoundException(Long id) {
        super("Probe not found with id: " + id);
    }
}
