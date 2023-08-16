package com.example.librarymanagement.model.support;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Address {
    String street;
    String city;
    String state;
    String zipCode;
    String country;
}
