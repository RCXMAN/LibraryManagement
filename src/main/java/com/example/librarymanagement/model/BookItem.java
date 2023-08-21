package com.example.librarymanagement.model;

import com.example.librarymanagement.model.enums.BookStatusEnum;
import com.example.librarymanagement.model.support.Book;
import com.example.librarymanagement.model.support.Rack;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookItem extends Book {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate borrowed;

    private LocalDate dueDate;

    private double price;

    @Enumerated(EnumType.STRING)
    private BookStatusEnum status;

    private Date dataOfPurchase;

    @OneToOne(cascade = {CascadeType.PERSIST})
    private Rack placedAt;
}
