package com.example.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoSuchSourceException extends RuntimeException {
    public NoSuchSourceException() {
        super("No such source found.");
    }
    public NoSuchSourceException(String message) {
        super(message);
    }
}
