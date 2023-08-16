package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Fine;
import com.example.librarymanagement.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FineRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private FineRepository repository;

    @Test
    void findByMember() {
        Member member = new Member();
        entityManager.persist(member);
        entityManager.persist(new Fine(LocalDate.now(), 50, member));
        entityManager.persist(new Fine(LocalDate.now(), 30, member));
        Pageable pageable = PageRequest.of(0, 5);

        Page<Fine> fine = repository.findByMember(member, pageable);

        assertEquals(fine.getTotalElements(), 2);
    }
}