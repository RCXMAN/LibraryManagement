package com.example.librarymanagement.service;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookLending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookLendingService {

    Page<BookLending> fetchMemberAllLending(Pageable pageable);
    BookLending createLending(BookItem book);
    BookLending prolongLending(BookItem bookItem);
    BookLending endLending(BookItem bookItem);
    BookLending getLendingDetail(Long lendingId);
}
