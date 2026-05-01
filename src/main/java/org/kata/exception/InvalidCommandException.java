package org.kata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCommandException extends RuntimeException {

    public InvalidCommandException(char character, int position) {
        super("Unknown command character '" + character + "' at position " + position);
    }
}
