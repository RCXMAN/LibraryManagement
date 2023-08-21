package com.example.librarymanagement.model;

import com.example.librarymanagement.model.enums.ReservationStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Data
public class BookReservation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private BookItem bookItem;

    @ManyToOne
    @JsonIgnore
    private Member member;

    private LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatusEnum status;
}
