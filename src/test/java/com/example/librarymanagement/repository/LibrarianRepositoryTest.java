package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.Librarian;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LibrarianRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private LibrarianRepository repository;

    @Test
    void findByUsername() {
        Librarian librarian = new Librarian();
        librarian.setUsername("joey");
        this.entityManager.persist(librarian);
        Librarian findLibrarian = repository.findByUsername("joey").get();
        Assertions.assertEquals(librarian, findLibrarian);
    }
}