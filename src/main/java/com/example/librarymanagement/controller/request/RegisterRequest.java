package com.example.librarymanagement.controller.request;

import com.example.librarymanagement.model.support.Person;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @JsonProperty(required = true)
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @JsonProperty(required = true)
    @NotBlank(message = "Password cannot be blank")
    private String password;
    private Person person;
}
