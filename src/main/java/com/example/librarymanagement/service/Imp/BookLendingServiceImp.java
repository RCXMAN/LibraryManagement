package com.example.librarymanagement.service.Imp;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.exception.BookRenewNotAllowedException;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.repository.BookLendingRepository;
import com.example.librarymanagement.service.BookLendingService;
import com.example.librarymanagement.service.MemberService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookLendingServiceImp implements BookLendingService {
    private final BookLendingRepository bookLendingRepository;
    private final MemberService memberService;

    @Override
    public Page<BookLending> fetchMemberAllLending(Pageable pageable) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        return bookLendingRepository.findByMember(member, pageable);
    }

    @Override
    public BookLending createLending(BookItem book) {
        BookLending lending = new BookLending();
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        lending.setBookItem(book);
        lending.setMember(member);
        lending.setCreationDate(book.getDueDate().minus(LibraryConstants.duePeriod));
        lending.setDueDate(book.getDueDate());
        bookLendingRepository.save(lending);

        return lending;
    }

    @Override
    public BookLending prolongLending(BookItem bookItem) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        BookLending bookLending = bookLendingRepository.findByMemberAndBookItemAndReturnDateIsNull(member, bookItem)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_LENDING));

        if (bookLending.getCreationDate().plus(LibraryConstants.duePeriod)
                .plus(LibraryConstants.duePeriod.multipliedBy(LibraryConstants.RENEW_LIMIT))
                .isEqual(bookLending.getDueDate()))
            throw new BookRenewNotAllowedException(ExceptionConstants.RENEW_NOT_ALLOWED);

        bookLending.setDueDate(bookLending.getDueDate().plus(LibraryConstants.duePeriod));

        return bookLending;
    }

    @Override
    public BookLending endLending(BookItem bookItem) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        BookLending bookLending = bookLendingRepository.findByMemberAndBookItemAndReturnDateIsNull(member, bookItem)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_LENDING));
        bookLending.setReturnDate(LocalDate.now());

        return bookLending;
    }

    @Override
    public BookLending getLendingDetail(Long lendingId){

        return bookLendingRepository.findById(lendingId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_LENDING));
    }

    @Override
    public boolean checkIfLendByCurrentUser(BookItem bookItem, String username) {
        Optional<BookLending> bookLending = bookLendingRepository.findByMemberAndBookItemAndReturnDateIsNull(
                memberService.getMember(username),
                bookItem);
        return bookLending.isPresent();
    }
}
