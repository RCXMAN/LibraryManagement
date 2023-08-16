package com.example.librarymanagement.service;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookReservationService {
    Page<BookReservation> fetchMemberAllReservations(Pageable pageable);

    BookReservation createReservation(BookItem bookItem);
    BookReservation completeReserve(BookItem bookItem);
    BookReservation cancelReserve(Long reservationId);
    boolean ifReserveByMemberBefore( BookItem bookItem);
    boolean ifReserveBefore(BookItem bookItem);
    BookReservation fetchReservationDetails(Long reservationId);
}
