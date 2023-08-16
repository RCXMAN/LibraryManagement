package com.example.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException() {
        super("Book is not available");
    }
    public BookNotAvailableException(String message) {
        super(message);
    }
}
