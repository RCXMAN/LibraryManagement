package com.example.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class BookRenewNotAllowedException extends RuntimeException{
    public BookRenewNotAllowedException() {
        super("book renew not allow");
    }

    public BookRenewNotAllowedException(String message) {
        super(message);
    }
}
