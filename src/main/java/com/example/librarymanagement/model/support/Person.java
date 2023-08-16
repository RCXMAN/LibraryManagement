package com.example.librarymanagement.model.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@MappedSuperclass
public class Person {
    @JsonProperty(required = true)
    @NotNull(message = "Name cannot be null")
    String name;

    @Embedded
    Address address = new Address();

    @Email(message = "Email should be valid")
    @JsonProperty(required = true)
    String email;

    String phone;
}