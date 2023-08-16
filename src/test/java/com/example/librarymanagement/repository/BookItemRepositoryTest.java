package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import com.example.librarymanagement.model.support.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookItemRepository repository;

    @Test
    void findAll() {
        List<BookItem> sampleBookItems = new ArrayList<>();
        BookItem bookItem1 = new BookItem();
        bookItem1.setTitle("Sample Book 1");
        Author author1 = new Author();
        author1.setName("Joey");
        bookItem1.setAuthors(author1);
        bookItem1.setBookSubject(BookSubjectEnum.FICTION);
        bookItem1.setPublicationDate(LocalDate.of(2023, 1, 1));
        sampleBookItems.add(bookItem1);

        BookItem bookItem2= new BookItem();
        bookItem2.setTitle("Sample Book 2");
        Author author2 = new Author();
        author2.setName("John");
        bookItem2.setAuthors(author1);
        bookItem2.setBookSubject(BookSubjectEnum.FICTION);
        bookItem2.setPublicationDate(LocalDate.of(2023, 1, 1));
        sampleBookItems.add(bookItem2);

        entityManager.persist(bookItem1);
        entityManager.persist(bookItem2);

        Pageable pageable = PageRequest.of(0, 10);

        Specification<BookItem> specification = BookItemSpecifications.searchByCriteria("Sample Book 1", null, BookSubjectEnum.FICTION, null);
        Page<BookItem> result = repository.findAll(specification, pageable);
        Specification<BookItem> specification2 = BookItemSpecifications.searchByCriteria(null, null, BookSubjectEnum.FICTION, null);
        Page<BookItem> result2 = repository.findAll(specification2, pageable);

        assertEquals(result.getTotalElements(), 1);
        assertEquals(result2.getTotalElements(), 2);
    }
}