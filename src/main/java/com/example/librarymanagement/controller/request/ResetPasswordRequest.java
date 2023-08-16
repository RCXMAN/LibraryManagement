package com.example.librarymanagement.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    private AuthenticationRequest authenticationRequest;
    @NotBlank(message = "Password cannot be blank")
    private String newPassword;
}
