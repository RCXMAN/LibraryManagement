package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookLendingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookLendingRepository repository;

    @Test
    void findByMember() {
        BookItem bookItem1 = new BookItem();
        BookItem bookItem2 = new BookItem();
        Member member = new Member();
        this.entityManager.persist(bookItem1);
        this.entityManager.persist(bookItem2);
        this.entityManager.persist(member);
        this.entityManager.persist(new BookLending(LocalDate.now(),
                LocalDate.of(2023, 8, 31), null, bookItem1, member));
        this.entityManager.persist(new BookLending(LocalDate.now(),
                LocalDate.of(2023, 7, 30), null, bookItem2, member));
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookLending> lendingPage = this.repository.findByMember(member, pageable);
        assertThat(lendingPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByMemberAndBookItemAndReturnDateIsNull() {
        BookItem bookItem = new BookItem();
        Member member = new Member();
        this.entityManager.persist(bookItem);
        this.entityManager.persist(member);
        this.entityManager.persist(new BookLending(LocalDate.now(),
                LocalDate.of(2023, 8, 31), null, bookItem, member));
        BookLending bookLending = this.repository.findByMemberAndBookItemAndReturnDateIsNull(member, bookItem).get();
        Assertions.assertThat(bookLending.getCreationDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(bookLending.getDueDate()).isEqualTo(LocalDate.of(2023, 8, 31));
    }
}