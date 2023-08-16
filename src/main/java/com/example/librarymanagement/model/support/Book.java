package com.example.librarymanagement.model.support;

import com.example.librarymanagement.model.enums.BookFormatEnum;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@MappedSuperclass
public class Book {
    private String ISBN;

    private String title;

    @Enumerated(EnumType.STRING)
    private BookSubjectEnum bookSubject;

    private LocalDate publicationDate;

    private int numberOfPages;

    @Enumerated(EnumType.STRING)
    private BookFormatEnum format;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Author authors;
}
