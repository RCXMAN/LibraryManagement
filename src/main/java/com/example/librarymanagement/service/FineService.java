package com.example.librarymanagement.service;

import com.example.librarymanagement.model.Fine;
import com.example.librarymanagement.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FineService {
    Page<Fine> getMemberFine(Pageable pageable);
    Fine collectFine(Member member, int days);
    double getAmount(Long fineId);
}
