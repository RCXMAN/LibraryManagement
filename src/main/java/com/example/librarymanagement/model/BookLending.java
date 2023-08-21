package com.example.librarymanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLending {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    @ManyToOne
    private BookItem bookItem;
    @ManyToOne
    @JsonIgnore
    private Member member;

    public BookLending(LocalDate creationDate, LocalDate dueDate, LocalDate returnDate, BookItem bookItem, Member member) {
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.bookItem = bookItem;
        this.member = member;
    }
}
