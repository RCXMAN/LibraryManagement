package com.example.librarymanagement.service;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.exception.BookNotAvailableException;
import com.example.librarymanagement.exception.MaxBooksCheckedOutException;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.enums.BookStatusEnum;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import com.example.librarymanagement.model.support.Author;
import com.example.librarymanagement.repository.BookItemRepository;
import com.example.librarymanagement.service.Imp.BookItemServiceImp;
import com.example.librarymanagement.service.Imp.EmailSenderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BookItemServiceTest {
    @Mock
    private BookItemRepository bookItemRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private BookLendingService bookLendingService;
    @Mock
    private BookReservationService bookReservationService;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private FineService fineService;
    @InjectMocks
    private BookItemServiceImp bookItemService;

    @Test
    void getAllBookItem() {
        Pageable page = Pageable.unpaged();
        when(bookItemRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        bookItemService.getAllBookItem(page);
        verify(bookItemRepository).findAll(any(Pageable.class));
    }

    @Test
    void getBookItem() {
        Long itemId = 1L;
        BookItem sampleBookItem = new BookItem();
        sampleBookItem.setId(itemId);

        when(bookItemRepository.findById(itemId)).thenReturn(Optional.of(sampleBookItem));

        BookItem result = bookItemService.getBookItem(itemId);
        Assertions.assertEquals(sampleBookItem, result);
    }

    @Test
    void testGetBookItem_ThrowsNoSuchSourceException() {
        Long itemId = 1L;
        when(bookItemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookItemService.getBookItem(itemId))
                .isInstanceOf(NoSuchSourceException.class)
                .hasMessageContaining(ExceptionConstants.NO_SUCH_BOOK);
    }

    @Test
    void addBookItem() {
        Long itemId = 1L;
        BookItem sampleBookItem = new BookItem();
        sampleBookItem.setId(itemId);
        sampleBookItem.setTitle("Sample Book");

        when(bookItemRepository.save(any(BookItem.class))).thenReturn(sampleBookItem);

        BookItem addedBookItem = bookItemService.addBookItem(sampleBookItem);

        Assertions.assertEquals(sampleBookItem, addedBookItem);
    }

    @Test
    void updateBookItem() {
        BookItem sampleBookItem = new BookItem();
        sampleBookItem.setId(1L);
        sampleBookItem.setTitle("Sample Book");

        when(bookItemRepository.save(any(BookItem.class))).thenReturn(sampleBookItem);

        BookItem updatedBookItem = bookItemService.updateBookItem(sampleBookItem);

        Assertions.assertEquals(sampleBookItem, updatedBookItem);
    }

    @Test
    void removeBook() {
        Long itemId = 1L;
        BookItem sampleBookItem = new BookItem();
        sampleBookItem.setId(itemId);
        sampleBookItem.setStatus(BookStatusEnum.AVAILABLE);

        when(bookItemRepository.findById(itemId)).thenReturn(Optional.of(sampleBookItem));

        bookItemService.removeBook(itemId);

        verify(bookItemRepository).findById(itemId);
        verify(bookItemRepository).deleteById(itemId);
    }

    @Test
    void testRemoveBook_ThrowsNoSuchSourceException() {
        Long itemId = 100L;

        when(bookItemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookItemService.removeBook(itemId))
                .isInstanceOf(NoSuchSourceException.class)
                .hasMessageContaining(ExceptionConstants.NO_SUCH_BOOK);
    }

    @Test
    void testRemoveBook_NonAvailableBook_ThrowsBookNotAvailableException() {
        Long itemId = 1L;
        BookItem nonAvailableBookItem = new BookItem();
        nonAvailableBookItem.setId(itemId);
        nonAvailableBookItem.setStatus(BookStatusEnum.LOANED);

        when(bookItemRepository.findById(itemId)).thenReturn(Optional.of(nonAvailableBookItem));

        assertThatThrownBy(() -> bookItemService.removeBook(itemId))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining(ExceptionConstants.NOT_AVAILABLE);
    }


    @Test
    void searchBookItem() {
        String title = "Sample Book";
        String authorName = "John Doe";
        BookSubjectEnum subject = BookSubjectEnum.FICTION;
        LocalDate publicationDate = LocalDate.of(2023, 1, 1);

        List<BookItem> sampleBookItems = new ArrayList<>();
        BookItem bookItem1 = new BookItem();
        bookItem1.setTitle("Sample Book 1");
        Author author1 = new Author();
        author1.setName("Joey");
        bookItem1.setAuthors(author1);
        bookItem1.setBookSubject(BookSubjectEnum.FICTION);
        bookItem1.setPublicationDate(LocalDate.of(2023, 1, 1));
        sampleBookItems.add(bookItem1);

        BookItem bookItem2= new BookItem();
        bookItem2.setTitle("Sample Book 2");
        Author author2 = new Author();
        author2.setName("John");
        bookItem2.setAuthors(author1);
        bookItem2.setBookSubject(BookSubjectEnum.FICTION);
        bookItem2.setPublicationDate(LocalDate.of(2023, 1, 1));
        sampleBookItems.add(bookItem2);

        Page<BookItem> samplePage = new PageImpl<>(sampleBookItems);

        when(bookItemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(samplePage);

        Page<BookItem> result = bookItemService.searchBookItem(title, authorName, subject, publicationDate, Pageable.unpaged());

        assertEquals(samplePage, result);
    }

    @Test
    @WithMockUser(username = "joey")
    void lendBook() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.AVAILABLE);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout("joey")).thenReturn(0); // Assuming initial checkouts are 0
        when(bookReservationService.ifReserveByMemberBefore(bookItem)).thenReturn(false);

        BookItem result = bookItemService.lendBook(1L);

        verify(bookLendingService, times(1)).createLending(bookItem);
        verify(memberService, times(1)).incrementTotalBooksCheckedOut("joey");
        verify(bookLendingService, times(1)).createLending(bookItem);

        Assertions.assertEquals(BookStatusEnum.LOANED, result.getStatus());
        Assertions.assertEquals(LocalDate.now().plus(LibraryConstants.duePeriod), result.getDueDate());
    }

    @Test
    @WithMockUser(username = "joey")
    void lendBook_Reserve_Before() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.AVAILABLE);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout("joey")).thenReturn(0); // Assuming initial checkouts are 0
        when(bookReservationService.ifReserveByMemberBefore(bookItem)).thenReturn(true);

        BookItem result = bookItemService.lendBook(1L);

        verify(bookReservationService, times(1)).completeReserve(bookItem);
        verify(memberService, times(1)).incrementTotalBooksCheckedOut("joey");
        verify(bookLendingService, times(1)).createLending(bookItem);

        Assertions.assertEquals(BookStatusEnum.LOANED, result.getStatus());
        Assertions.assertEquals(LocalDate.now().plus(LibraryConstants.duePeriod), result.getDueDate());
    }

    @Test
    @WithMockUser(username = "joey")
    void lendBook_MAX_BOOK_LIMIT() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.AVAILABLE);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout("joey")).thenReturn(LibraryConstants.MAX_BOOK_LIMIT);

        assertThatThrownBy(() -> bookItemService.lendBook(1L))
                .isInstanceOf(MaxBooksCheckedOutException.class)
                .hasMessageContaining(ExceptionConstants.MAX_BOOKS_CHECKED_OUT);
    }

    @Test
    @WithMockUser(username = "joey")
    void lendBook_Book_Not_Available() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.LOST);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout("joey")).thenReturn(0);

        assertThatThrownBy(() -> bookItemService.lendBook(1L))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining(ExceptionConstants.NOT_AVAILABLE);
    }

    @Test
    void renewBook() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        BookLending lending = new BookLending();
        LocalDate newDueDate = LocalDate.now().plusDays(14);
        lending.setDueDate(newDueDate);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(bookLendingService.prolongLending(bookItem)).thenReturn(lending);

        BookItem renewedBook = bookItemService.renewBook(1L);
        Assertions.assertEquals(newDueDate, renewedBook.getDueDate());
    }

    @Test
    @WithMockUser(username = "joey")
    void returnBook() {
        Member member = new Member();
        when(memberService.getMember(anyString())).thenReturn(member);

        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));

        LocalDate dueDate = LocalDate.now().minusDays(7);
        LocalDate returnDate = LocalDate.now();
        BookLending lending = new BookLending();
        lending.setReturnDate(returnDate);
        lending.setDueDate(dueDate);
        when(bookLendingService.endLending(bookItem)).thenReturn(lending);
        when(bookReservationService.ifReserveBefore(bookItem)).thenReturn(false);

        BookItem returnedBook = bookItemService.returnBook(1L);
        verify(fineService, times(1)).collectFine(member, 7);
        verify(emailSenderService, times(0)).sendEmailToUser(any(Member.class),
                anyString(), anyString());
        Assertions.assertEquals(BookStatusEnum.AVAILABLE, returnedBook.getStatus());
    }

    @Test
    @WithMockUser(username = "joey")
    void returnBook_NO_FINE_AND_RESERVED_BEFORE() {
        Member member = new Member();
        when(memberService.getMember(anyString())).thenReturn(member);

        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));

        LocalDate dueDate = LocalDate.now().plusDays(7);
        LocalDate returnDate = LocalDate.now();
        BookLending lending = new BookLending();
        lending.setReturnDate(returnDate);
        lending.setDueDate(dueDate);
        when(bookLendingService.endLending(bookItem)).thenReturn(lending);
        when(bookReservationService.ifReserveBefore(bookItem)).thenReturn(true);

        BookItem returnedBook = bookItemService.returnBook(1L);
        verify(fineService, times(0)).collectFine(member, 7);
        verify(emailSenderService, times(1)).sendEmailToUser(any(Member.class),
                anyString(), anyString());
        Assertions.assertEquals(BookStatusEnum.RESERVED, returnedBook.getStatus());
    }

    @Test
    @WithMockUser("joey")
    void reserveBook() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.AVAILABLE);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout(anyString())).thenReturn(0);

        BookItem reserveBook = bookItemService.reserveBook(1L);

        verify(bookReservationService, times(1)).createReservation(bookItem);
        Assertions.assertEquals(BookStatusEnum.RESERVED, reserveBook.getStatus());
    }

    @Test
    @WithMockUser("joey")
    void reserveBook_LOAN() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.LOANED);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout(anyString())).thenReturn(0);

        BookItem reserveBook = bookItemService.reserveBook(1L);

        verify(bookReservationService, times(1)).createReservation(bookItem);
        Assertions.assertEquals(BookStatusEnum.RESERVED, reserveBook.getStatus());
    }

    @Test
    @WithMockUser("joey")
    void reserveBook_Max_Books_CheckedOut() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.AVAILABLE);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout(anyString())).thenReturn(LibraryConstants.MAX_BOOK_LIMIT);

        assertThatThrownBy(() -> bookItemService.reserveBook(1L))
                .isInstanceOf(MaxBooksCheckedOutException.class)
                .hasMessageContaining(ExceptionConstants.MAX_BOOKS_CHECKED_OUT);
    }

    @Test
    @WithMockUser("joey")
    void reserveBook_NOT_AVAILABLE_RESERVED() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.RESERVED);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout(anyString())).thenReturn(0);

        assertThatThrownBy(() -> bookItemService.reserveBook(1L))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining(ExceptionConstants.NOT_AVAILABLE_RESERVED);
    }

    @Test
    @WithMockUser("joey")
    void reserveBook_NOT_AVAILABLE_LOST() {
        BookItem bookItem = new BookItem();
        bookItem.setId(1L);
        bookItem.setStatus(BookStatusEnum.LOST);

        when(bookItemRepository.findById(1L)).thenReturn(Optional.of(bookItem));
        when(memberService.getTotalBooksCheckout(anyString())).thenReturn(0);

        assertThatThrownBy(() -> bookItemService.reserveBook(1L))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining(ExceptionConstants.NOT_AVAILABLE_LOST);
    }

}