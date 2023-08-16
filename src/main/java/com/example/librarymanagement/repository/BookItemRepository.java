package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface BookItemRepository extends JpaRepository<BookItem, Long>, JpaSpecificationExecutor<BookItem> {
    Page<BookItem> findAll(Specification<BookItem> specification, Pageable pageable);
}



