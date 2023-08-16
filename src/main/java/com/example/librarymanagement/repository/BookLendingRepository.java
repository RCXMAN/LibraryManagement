package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookLendingRepository extends JpaRepository<BookLending, Long> {
    Page<BookLending> findByMember(Member member, Pageable pageable);
    Optional<BookLending> findByMemberAndBookItemAndReturnDateIsNull(Member member, BookItem bookItem);
}
