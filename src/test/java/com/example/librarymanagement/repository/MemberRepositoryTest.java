package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private MemberRepository repository;

    @Test
    void findByUsername() {
        Member member = new Member();
        member.setUsername("joey");
        this.entityManager.persist(member);
        Member findMember = repository.findByUsername("joey").get();

        Assertions.assertEquals(member, findMember);
    }
}