package com.example.librarymanagement.service.Imp;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.exception.BookNotAvailableException;
import com.example.librarymanagement.exception.MaxBooksCheckedOutException;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.model.enums.BookStatusEnum;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import com.example.librarymanagement.repository.BookItemRepository;
import com.example.librarymanagement.service.*;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.example.librarymanagement.repository.BookItemSpecifications.searchByCriteria;

@Service
@RequiredArgsConstructor
public class BookItemServiceImp implements BookItemService {
    private final BookItemRepository bookItemRepository;
    private final MemberService memberService;
    private final BookLendingService bookLendingService;
    private final BookReservationService bookReservationService;
    private final EmailSenderService emailSenderService;
    private final FineService fineService;
    @Override
    public Page<BookItem> getAllBookItem(Pageable pageable) {
        return bookItemRepository.findAll(pageable);
    }
    @Override
    public BookItem getBookItem(Long itemId) {
        return bookItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_BOOK));
    }
    @Override
    public BookItem addBookItem(BookItem bookItem) {
        bookItem.setStatus(BookStatusEnum.AVAILABLE);
        return bookItemRepository.save(bookItem);
    }
    @Override
    public BookItem updateBookItem(BookItem bookItem) {
        return bookItemRepository.save(bookItem);
    }
    @Override
    public void removeBook(Long itemId) {
        BookItem book = bookItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_BOOK));
        if (book.getStatus() != BookStatusEnum.AVAILABLE) {
            throw new BookNotAvailableException(ExceptionConstants.NOT_AVAILABLE_DELETE);
        }
        bookItemRepository.deleteById(itemId);
    }
    @Override
    public Page<BookItem> searchBookItem(String title, String authorName, BookSubjectEnum subject, LocalDate publicationDate,
                                         Pageable pageable) {

        Specification<BookItem> specification = searchByCriteria(title, authorName, subject, publicationDate);
        return bookItemRepository.findAll(specification, pageable);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BookItem lendBook(Long itemId) {
        String username = SecurityUtils.getCurrentUsername();
        BookItem bookItem = bookItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_BOOK));

        if (memberService.getTotalBooksCheckout(username) >= LibraryConstants.MAX_BOOK_LIMIT) {
            throw new MaxBooksCheckedOutException(ExceptionConstants.MAX_BOOKS_CHECKED_OUT);
        }

        if (bookItem.getStatus() != BookStatusEnum.AVAILABLE) {
            throw new BookNotAvailableException(ExceptionConstants.NOT_AVAILABLE);
        }

        bookItem.setStatus(BookStatusEnum.LOANED);
        bookItem.setDueDate(LocalDate.now().plus(LibraryConstants.duePeriod));

        if (bookReservationService.ifReserveByMemberBefore(bookItem)) {
            bookReservationService.completeReserve(bookItem);
        }

        memberService.incrementTotalBooksCheckedOut(username);
        bookItemRepository.save(bookItem);
        bookLendingService.createLending(bookItem);

        return bookItem;
    }
    @Override
    public BookItem renewBook(Long itemId) {
        BookItem book = bookItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_BOOK));
        BookLending lending = bookLendingService.prolongLending(book);
        book.setDueDate(lending.getDueDate());
        bookItemRepository.save(book);
        return book;
    }
    @Override
    public BookItem returnBook(Long itemId) {
        String username = SecurityUtils.getCurrentUsername();
        BookItem book = bookItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_BOOK));
        BookLending bookLending = bookLendingService.endLending(book);

        if (bookLending.getDueDate().isBefore(bookLending.getReturnDate())) {
            fineService.collectFine(
                    memberService.getMember(username),
                    bookLending.getReturnDate().getDayOfMonth() - bookLending.getDueDate().getDayOfMonth());
        } else {
            memberService.decrementTotalBooksCheckedOut(username);
        }

        if (bookReservationService.ifReserveBefore(book)) {
            book.setStatus(BookStatusEnum.RESERVED);
            emailSenderService.sendEmailToUser(
                    memberService.getMember(username),
                    LibraryConstants.RESERVATION_SUBJECT,
                    LibraryConstants.RESERVATION_BODY);
        } else {
            book.setStatus(BookStatusEnum.AVAILABLE);
        }

        bookItemRepository.save(book);

        return book;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BookItem reserveBook(Long itemId) {
        String username = SecurityUtils.getCurrentUsername();
        BookItem book = bookItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_BOOK));
        if (memberService.getTotalBooksCheckout(username) >= LibraryConstants.MAX_BOOK_LIMIT) {
            throw new MaxBooksCheckedOutException();
        }

        if (book.getStatus() == BookStatusEnum.RESERVED) {
            throw new BookNotAvailableException(ExceptionConstants.NOT_AVAILABLE_RESERVED);
        }

        if (book.getStatus() == BookStatusEnum.LOST) {
            throw new BookNotAvailableException(ExceptionConstants.NOT_AVAILABLE_LOST);
        }

        if (book.getStatus() == BookStatusEnum.AVAILABLE) {
            book.setStatus(BookStatusEnum.RESERVED);
            bookItemRepository.save(book);
            bookReservationService.createReservation(book);
        } else if (book.getStatus() == BookStatusEnum.LOANED) {
            if (bookLendingService.checkIfLendByCurrentUser(book, username)) {
                throw new BookNotAvailableException(ExceptionConstants.ALREADY_LEND_BEFORE);
            }
            book.setStatus(BookStatusEnum.RESERVED);
            bookItemRepository.save(book);
            bookReservationService.createReservation(book);
        }

        return book;
    }
}
