package com.example.librarymanagement.service;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookReservation;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.enums.BookStatusEnum;
import com.example.librarymanagement.model.enums.ReservationStatusEnum;
import com.example.librarymanagement.repository.BookReservationRepository;
import com.example.librarymanagement.service.Imp.BookReservationServiceImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WithMockUser(username = MemberServiceImpTest.TEST_USERNAME)
class BookReservationServiceImpTest {
    public static final String TEST_USERNAME = "joey";
    @Mock
    private BookReservationRepository bookReservationRepository;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private BookReservationServiceImp bookReservationServiceImp;

    @Test
    void fetchMemberAllReservations() {
        Member member = new Member();
        when(memberService.getMember(TEST_USERNAME)).thenReturn(member);

        BookReservation reservation1 = new BookReservation();
        reservation1.setStatus(ReservationStatusEnum.WAITING);

        BookReservation reservation2 = new BookReservation();
        reservation2.setStatus(ReservationStatusEnum.COMPLETED);

        List<BookReservation> allReservations = List.of(reservation1, reservation2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<BookReservation> page = new PageImpl<>(allReservations, pageable, allReservations.size());
        when(bookReservationRepository.findByMember(member, pageable)).thenReturn(page);

        Page<BookReservation> result = bookReservationServiceImp.fetchMemberAllReservations(pageable);

        List<ReservationStatusEnum> statusList = result.stream().map(BookReservation::getStatus).collect(Collectors.toList());
        List<ReservationStatusEnum> expectedStatusList = List.of(ReservationStatusEnum.WAITING, ReservationStatusEnum.COMPLETED);
        assertEquals(expectedStatusList, statusList);
    }

    @Test
    void createReservation() {
        Member member = new Member();
        when(memberService.getMember(TEST_USERNAME)).thenReturn(member);

        BookItem bookItem = new BookItem();

        BookReservation expectedReservation = new BookReservation();
        expectedReservation.setMember(member);
        expectedReservation.setBookItem(bookItem);
        expectedReservation.setCreationDate(LocalDate.now());
        expectedReservation.setStatus(ReservationStatusEnum.WAITING);

        when(bookReservationRepository.save(expectedReservation)).thenReturn(expectedReservation);

        BookReservation actualReservation = bookReservationServiceImp.createReservation(bookItem);

        Assertions.assertEquals(expectedReservation, actualReservation);
        Assertions.assertEquals(ReservationStatusEnum.WAITING, actualReservation.getStatus());
    }

    @Test
    void cancelReserve() {
        Long reservationId = 1L;

        BookReservation bookReservation = new BookReservation();
        bookReservation.setId(reservationId);
        bookReservation.setStatus(ReservationStatusEnum.WAITING);

        BookItem bookItem = new BookItem();
        bookItem.setStatus(BookStatusEnum.RESERVED);
        bookReservation.setBookItem(bookItem);

        when(bookReservationRepository.findById(reservationId)).thenReturn(Optional.of(bookReservation));

        BookReservation canceledReservation = bookReservationServiceImp.cancelReserve(reservationId);
        assertEquals(ReservationStatusEnum.CANCELED, canceledReservation.getStatus());
        assertEquals(BookStatusEnum.LOANED, bookItem.getStatus());
    }

    @Test
    void fetchReservationDetails() {
        Long reservationId = 1L;
        BookReservation expectedReservation = new BookReservation();
        expectedReservation.setId(reservationId);

        when(bookReservationRepository.findById(reservationId)).thenReturn(Optional.of(expectedReservation));

        BookReservation result = bookReservationServiceImp.fetchReservationDetails(reservationId);

        Assertions.assertEquals(expectedReservation, result);
    }

    @Test
    void testFetchReservationDetailsNotFound() {
        Long reservationId = 1L;

        when(bookReservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookReservationServiceImp.fetchReservationDetails(reservationId))
                .isInstanceOf(NoSuchSourceException.class)
                .hasMessageContaining(ExceptionConstants.NO_SUCH_RESERVATION);
    }

    @Test
    void completeReserve() {
        BookItem bookItem = new BookItem();
        Member member = new Member();
        BookReservation bookReservation = new BookReservation();
        bookReservation.setStatus(ReservationStatusEnum.WAITING);
        when(memberService.getMember(TEST_USERNAME)).thenReturn(member);
        when(bookReservationRepository
                .findByMemberAndBookItemAndStatus(member, bookItem, ReservationStatusEnum.WAITING))
                .thenReturn(Optional.of(bookReservation));

        BookReservation completeReserve = bookReservationServiceImp.completeReserve(bookItem);
        Assertions.assertEquals(ReservationStatusEnum.COMPLETED, completeReserve.getStatus());
    }

    @Test
    void ifReserveByMemberBefore() {
        Member member = new Member();
        when(memberService.getMember(TEST_USERNAME)).thenReturn(member);
        BookItem bookItem = new BookItem();

        when(bookReservationRepository.existsByMemberAndBookItemAndStatus(member, bookItem, ReservationStatusEnum.WAITING))
                .thenReturn(true);
        boolean result = bookReservationServiceImp.ifReserveByMemberBefore(bookItem);
        assertTrue(result);
    }

    @Test
    void ifReserveBefore() {
        BookItem bookItem = new BookItem();

        when(bookReservationRepository.existsByBookItemAndStatus(bookItem, ReservationStatusEnum.WAITING))
                .thenReturn(true);
        boolean result = bookReservationServiceImp.ifReserveBefore(bookItem);
        assertTrue(result);
    }
}