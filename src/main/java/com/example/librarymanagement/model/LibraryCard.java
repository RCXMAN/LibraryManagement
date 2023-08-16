package com.example.librarymanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import java.util.Date;

@Entity
@Data
public class LibraryCard {
    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    private String cardNumber;

    private Date issued;

    private boolean active;
}
