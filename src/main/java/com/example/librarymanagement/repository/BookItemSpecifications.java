package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookItemSpecifications {
    public static Specification<BookItem> searchByCriteria(String title, String authorName, BookSubjectEnum subject, LocalDate publicationDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("title"), title));
            }
            if (authorName != null && !authorName.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("authors").get("name"), authorName));
            }
            if (subject != null && !subject.name().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("bookSubject"), subject));
            }
            if (publicationDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("publicationDate"), publicationDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
