package com.example.librarymanagement.model.support;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Rack {
    @Id
    private Long id;

    private int number;

    private String locationIdentifier;
}
