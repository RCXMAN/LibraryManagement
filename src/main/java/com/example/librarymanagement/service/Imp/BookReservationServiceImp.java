package com.example.librarymanagement.service.Imp;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookReservation;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.enums.ReservationStatusEnum;
import com.example.librarymanagement.repository.BookReservationRepository;
import com.example.librarymanagement.service.BookReservationService;
import com.example.librarymanagement.service.MemberService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReservationServiceImp implements BookReservationService {
    private final BookReservationRepository bookReservationRepository;
    private final MemberService memberService;
    @Override
    public Page<BookReservation> fetchMemberAllReservations(Pageable pageable) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        Page<BookReservation> page = bookReservationRepository.findByMember(member, pageable);
        List<BookReservation> sortedReservations = page.getContent()
                .stream()
                .sorted(Comparator.comparing(reservation -> {
                    if (reservation.getStatus() == ReservationStatusEnum.WAITING) {
                        return 0; // Status "WAITING" will have lower sort value, so it appears first.
                    } else {
                        return 1; // Other statuses will have higher sort value, so they appear after "WAITING".
                    }
                }))
                .collect(Collectors.toList());

        return new PageImpl<>(sortedReservations, pageable, page.getTotalElements());
    }
    @Override
    public BookReservation createReservation(BookItem bookItem) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        BookReservation bookReservation = new BookReservation();
        bookReservation.setMember(member);
        bookReservation.setBookItem(bookItem);
        bookReservation.setCreationDate(LocalDate.now());
        bookReservation.setStatus(ReservationStatusEnum.WAITING);
        bookReservationRepository.save(bookReservation);

        return bookReservation;
    }
    @Override
    public BookReservation cancelReserve(Long reservationId) {
        BookReservation bookReservation = bookReservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_RESERVATION));

        bookReservation.setStatus(ReservationStatusEnum.CANCELED);
        bookReservationRepository.save(bookReservation);

        return bookReservation;
    }
    @Override
    public BookReservation fetchReservationDetails(Long reservationId) {
        return bookReservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_RESERVATION));
    }
    @Override
    public BookReservation completeReserve(BookItem bookItem) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        BookReservation bookReservation = bookReservationRepository
                .findByMemberAndBookItemAndStatus(member, bookItem, ReservationStatusEnum.WAITING)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_RESERVATION));

        bookReservation.setStatus(ReservationStatusEnum.COMPLETED);
        bookReservationRepository.save(bookReservation);

        return bookReservation;
    }
    @Override
    public boolean ifReserveByMemberBefore(BookItem bookItem) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        return bookReservationRepository.existsByMemberAndBookItemAndStatus(member, bookItem, ReservationStatusEnum.WAITING);
    }
    @Override
    public boolean ifReserveBefore(BookItem bookItem) {
        return bookReservationRepository.existsByBookItemAndStatus(bookItem, ReservationStatusEnum.WAITING);
    }
}
