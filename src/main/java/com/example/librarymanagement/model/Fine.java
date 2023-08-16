package com.example.librarymanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fine {
    @Id
    @GeneratedValue
    Long id;
    LocalDate createDate;
    double amount;
    @ManyToOne
    private Member member;

    public Fine(LocalDate createDate, double amount, Member member) {
        this.createDate = createDate;
        this.amount = amount;
        this.member = member;
    }
}
