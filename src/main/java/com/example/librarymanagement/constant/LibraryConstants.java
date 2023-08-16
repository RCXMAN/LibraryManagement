package com.example.librarymanagement.constant;

import java.time.Period;

public class LibraryConstants {
    public static final int MAX_BOOK_LIMIT = 10;
    public static final Period duePeriod = Period.ofWeeks(2);
    public static final int RENEW_LIMIT = 1;
    public static final String INFO_EMAIL = "cixiang66@gmail.com";
    public static final String RESERVATION_SUBJECT = "Reservation Info";
    public static final String RESERVATION_BODY = "The book you reserved before has become available.";
    public static final double FINE_PER_DAY_OVERDUE = 1.5;

}
