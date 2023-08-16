package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookReservation;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.enums.ReservationStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {
    Optional<BookReservation> findByMemberAndBookItemAndStatus(Member member, BookItem bookItem, ReservationStatusEnum status);
    boolean existsByMemberAndBookItemAndStatus(Member member, BookItem bookItem, ReservationStatusEnum status);
    boolean existsByBookItemAndStatus(BookItem bookItem, ReservationStatusEnum status);
    Page<BookReservation> findByMember(Member member, Pageable pageable);
}
