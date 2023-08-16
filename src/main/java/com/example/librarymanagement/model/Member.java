package com.example.librarymanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Member extends User {
    private LocalDate dataOfMembership;

    private int totalBooksCheckedOut;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<Fine> fines;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<BookLending> lending;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<BookReservation> reservations = new ArrayList<>();
}
