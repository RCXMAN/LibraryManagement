package com.example.librarymanagement.controller;

import com.example.librarymanagement.controller.request.AuthenticationRequest;
import com.example.librarymanagement.controller.request.RegisterRequest;
import com.example.librarymanagement.controller.request.ResetPasswordRequest;
import com.example.librarymanagement.controller.response.AuthenticationResponse;
import com.example.librarymanagement.service.Imp.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library/auth")
@CrossOrigin
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping(path = "/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

