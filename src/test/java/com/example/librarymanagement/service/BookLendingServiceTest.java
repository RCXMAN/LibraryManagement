package com.example.librarymanagement.service;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.exception.BookRenewNotAllowedException;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.repository.BookLendingRepository;
import com.example.librarymanagement.service.Imp.BookLendingServiceImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BookLendingServiceTest {
    @Mock
    private BookLendingRepository bookLendingRepository;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private BookLendingServiceImp bookLendingServiceImp;

    @Test
    @WithMockUser(username = "joey")
    void fetchMemberAllLending() {
        Member sampleMember = new Member();
        sampleMember.setUsername("joey");

        List<BookLending> sampleBookLending = new ArrayList<>(List.of(new BookLending(), new BookLending()));

        Page<BookLending> samplePage = new PageImpl<>(sampleBookLending);

        when(memberService.getMember("joey")).thenReturn(sampleMember);

        when(bookLendingRepository.findByMember(sampleMember, Pageable.unpaged())).thenReturn(samplePage);

        Page<BookLending> result = bookLendingServiceImp.fetchMemberAllLending(Pageable.unpaged());

        assertEquals(samplePage, result);
    }

    @Test
    @WithMockUser(username = "joey")
    void createLending() {
        BookItem bookItem = new BookItem();
        bookItem.setDueDate(LocalDate.now());
        bookLendingServiceImp.createLending(bookItem);

        BookLending lending = new BookLending();
        lending.setBookItem(bookItem);
        lending.setCreationDate(bookItem.getDueDate().minus(LibraryConstants.duePeriod));
        lending.setDueDate(bookItem.getDueDate());

        verify(memberService, times(1)).getMember("joey");
        verify(bookLendingRepository, times(1)).save(lending);
    }

    @Test
    @WithMockUser(username = "joey")
    void prolongLending() {
        BookItem bookItem = new BookItem();
        bookItem.setDueDate(LocalDate.now().plus(LibraryConstants.duePeriod));

        Member member = new Member();
        when(memberService.getMember("joey")).thenReturn(member);

        BookLending bookLending = new BookLending();
        bookLending.setCreationDate(LocalDate.now());
        bookLending.setDueDate(LocalDate.now().plus(LibraryConstants.duePeriod));

        when(bookLendingRepository.findByMemberAndBookItemAndReturnDateIsNull(member, bookItem))
                .thenReturn(Optional.of(bookLending));

        bookLendingServiceImp.prolongLending(bookItem);

        Assertions.assertEquals(LocalDate.now().plus(LibraryConstants.duePeriod).plus(LibraryConstants.duePeriod), bookLending.getDueDate());
    }

    @Test
    @WithMockUser(username = "joey")
    void prolongLending_RENEW_LIMIT() {
        BookItem bookItem = new BookItem();
        bookItem.setDueDate(LocalDate.now().plus(LibraryConstants.duePeriod));

        Member member = new Member();
        when(memberService.getMember("joey")).thenReturn(member);

        BookLending bookLending = new BookLending();
        bookLending.setCreationDate(LocalDate.now());
        bookLending.setDueDate(LocalDate.now().plus(LibraryConstants.duePeriod).plus(LibraryConstants.duePeriod));

        when(bookLendingRepository.findByMemberAndBookItemAndReturnDateIsNull(member, bookItem))
                .thenReturn(Optional.of(bookLending));

        assertThatThrownBy(() -> bookLendingServiceImp.prolongLending(bookItem))
                .isInstanceOf(BookRenewNotAllowedException.class)
                .hasMessageContaining(ExceptionConstants.RENEW_NOT_ALLOWED);
    }

    @Test
    @WithMockUser(username = "joey")
    void endLending() {
        Member member = new Member();
        BookItem bookItem = new BookItem();
        BookLending bookLending = new BookLending();

        when(memberService.getMember("joey")).thenReturn(member);
        when(bookLendingRepository.findByMemberAndBookItemAndReturnDateIsNull(member, bookItem))
                .thenReturn(Optional.of(bookLending));

        BookLending endLending = bookLendingServiceImp.endLending(bookItem);
        Assertions.assertEquals(LocalDate.now(), endLending.getReturnDate());
    }

    @Test
    @WithMockUser(username = "joey")
    void endLending_NO_SUCH_LENDING() {
        Member member = new Member();
        BookItem bookItem = new BookItem();
        BookLending bookLending = new BookLending();

        when(memberService.getMember("joey")).thenReturn(member);
        when(bookLendingRepository.findByMemberAndBookItemAndReturnDateIsNull(member, bookItem))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookLendingServiceImp.endLending(bookItem))
                .isInstanceOf(NoSuchSourceException.class)
                .hasMessageContaining(ExceptionConstants.NO_SUCH_LENDING);
    }

    @Test
    void getLendingDetail() {
        Long lendingId = 1L;
        BookLending sampleBookLending = new BookLending();
        sampleBookLending.setId(lendingId);

        when(bookLendingRepository.findById(lendingId)).thenReturn(Optional.of(sampleBookLending));

        BookLending result = bookLendingServiceImp.getLendingDetail(lendingId);

        assertNotNull(result);
        Assertions.assertEquals(sampleBookLending, result);
    }

    @Test
    void testGetLendingDetail_ThrowsNoSuchSourceException() {
        Long lendingId = 1L;

        when(bookLendingRepository.findById(lendingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookLendingServiceImp.getLendingDetail(lendingId))
                .isInstanceOf(NoSuchSourceException.class)
                .hasMessageContaining(ExceptionConstants.NO_SUCH_LENDING);
    }
}