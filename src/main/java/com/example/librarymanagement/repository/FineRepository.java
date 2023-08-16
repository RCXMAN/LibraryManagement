package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Fine;
import com.example.librarymanagement.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    Page<Fine> findByMember(Member member, Pageable pageable);
}
