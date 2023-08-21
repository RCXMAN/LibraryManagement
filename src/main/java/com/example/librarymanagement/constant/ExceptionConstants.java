package com.example.librarymanagement.constant;

public class ExceptionConstants {
    public static final String NO_SUCH_BOOK = "No such book found.";
    public static final String NO_SUCH_LENDING = "No such lending found.";
    public static final String NO_SUCH_RESERVATION = "No such reservation found.";
    public static final String NO_SUCH_USER = "The User is not exist.";
    public static final String NO_SUCH_MEMBER = "The Member is not exist.";
    public static final String NO_SUCH_LIBRARIAN = "The Member is not exist.";
    public static final String NO_SUCH_FINE = "The Fine is not exist.";
    public static final String NOT_AVAILABLE = "Book is not available";
    public static final String NOT_AVAILABLE_RESERVED = "Book is already reserved";
    public static final String NOT_AVAILABLE_LOST = "Book is already lost";
    public static final String RENEW_NOT_ALLOWED = "You have already renewed this book " + LibraryConstants.RENEW_LIMIT + " time";
    public static final String MAX_BOOKS_CHECKED_OUT = "Member has already checked out maximum number of books";
    public static final String ALREADY_LEND_BEFORE = "You can't reserve the book you have lend";
    public static final String NOT_AVAILABLE_DELETE = "Can't delete a book which state is not available";
}
