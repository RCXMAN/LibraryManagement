package com.example.librarymanagement.service.Imp;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.Fine;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.repository.FineRepository;
import com.example.librarymanagement.service.FineService;
import com.example.librarymanagement.service.MemberService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FineServiceImp implements FineService {
    private final FineRepository fineRepository;
    private final MemberService memberService;

    @Override
    public Page<Fine> getMemberFine(Pageable pageable) {
        Member member = memberService.getMember(SecurityUtils.getCurrentUsername());
        return fineRepository.findByMember(member, pageable);
    }
    @Override
    public Fine collectFine(Member member, int days) {
        Fine fine = new Fine();
        fine.setAmount(LibraryConstants.FINE_PER_DAY_OVERDUE * days);
        fine.setMember(member);
        fine.setCreateDate(LocalDate.now());
        return fineRepository.save(fine);
    }
    @Override
    public double getAmount(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_FINE));
        return fine.getAmount();
    }
}
