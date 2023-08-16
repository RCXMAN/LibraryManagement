package com.example.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class MaxBooksCheckedOutException extends RuntimeException {
    public MaxBooksCheckedOutException() {
        super("Member has already checked out maximum number of books");
    }

    public MaxBooksCheckedOutException(String message) {
        super(message);
    }
}
