package com.example.librarymanagement.service;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BookItemService {
    Page<BookItem> getAllBookItem(Pageable pageable);
    BookItem getBookItem(Long itemId);
    BookItem addBookItem(BookItem bookItem);
    BookItem updateBookItem(BookItem bookItem);
    void removeBook(Long id);
    Page<BookItem> searchBookItem(String title, String authorName, BookSubjectEnum subject, LocalDate publicationDate, Pageable pageable);
    BookItem lendBook(Long item);
    BookItem renewBook(Long itemId);
    BookItem returnBook(Long itemId);
    BookItem reserveBook(Long item);
}
