package com.example.librarymanagement.service;

import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.Fine;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.repository.FineRepository;
import com.example.librarymanagement.service.Imp.FineServiceImp;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WithMockUser(username = "joey")
class FineServiceTest {

    @Mock
    private FineRepository fineRepository;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private FineServiceImp fineServiceImp;

    @Test
    void getMemberFine() {
        Member sampleMember = new Member();
        sampleMember.setUsername("joey");
        when(memberService.getMember("joey")).thenReturn(sampleMember);

        List<Fine> listFine = new ArrayList<>(List.of(new Fine(), new Fine()));
        Page<Fine> samplePage = new PageImpl<>(listFine);

        when(fineRepository.findByMember(sampleMember, Pageable.unpaged())).thenReturn(samplePage);

        Page<Fine> result = fineServiceImp.getMemberFine(Pageable.unpaged());

        assertEquals(samplePage, result);
    }

    @Test
    void collectFine() {
        Member sampleMember = new Member();
        sampleMember.setUsername("joey");
        int days = 5;
        double expectedAmount = LibraryConstants.FINE_PER_DAY_OVERDUE * days;

        Fine savedFine = new Fine();
        savedFine.setAmount(expectedAmount);
        savedFine.setMember(sampleMember);
        savedFine.setCreateDate(LocalDate.now());

        when(fineRepository.save(savedFine)).thenReturn(savedFine);

        Assertions.assertEquals(savedFine, fineServiceImp.collectFine(sampleMember, days));
    }

    @Test
    void getAmount() {
        Long fineId = 1L;
        Fine fine = new Fine();
        fine.setId(fineId);
        fine.setAmount(100);

        when(fineRepository.findById(fineId)).thenReturn(Optional.of(fine));

        double amount = fineServiceImp.getAmount(fineId);

        assertEquals(100, amount);
    }

    @Test
    void testGetAmountNoSuchFine() {
        Long fineId = 1L;

        when(fineRepository.findById(fineId)).thenReturn(Optional.empty());

        assertThrows(NoSuchSourceException.class, () -> fineServiceImp.getAmount(fineId));
    }
}