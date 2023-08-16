package com.example.librarymanagement.config;


import com.example.librarymanagement.exception.BookNotAvailableException;
import com.example.librarymanagement.exception.BookRenewNotAllowedException;
import com.example.librarymanagement.exception.MaxBooksCheckedOutException;
import com.example.librarymanagement.exception.NoSuchSourceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(BookNotAvailableException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionRestResponse handleNotAvailableException(BookNotAvailableException exception) {
        return new ExceptionRestResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ExceptionHandler(BookRenewNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionRestResponse handleRenewNotAllowException(BookRenewNotAllowedException exception) {
        return new ExceptionRestResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ExceptionHandler(MaxBooksCheckedOutException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionRestResponse handleMaxCheckOutException(MaxBooksCheckedOutException exception) {
        return new ExceptionRestResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ExceptionHandler(NoSuchSourceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionRestResponse handleNoSuchSourceException(NoSuchSourceException exception) {
        return new ExceptionRestResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    public record ExceptionRestResponse(int code, String message) {}
}
