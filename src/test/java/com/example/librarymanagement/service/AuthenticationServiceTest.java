package com.example.librarymanagement.service;

import com.example.librarymanagement.controller.request.AuthenticationRequest;
import com.example.librarymanagement.controller.request.RegisterRequest;
import com.example.librarymanagement.controller.request.ResetPasswordRequest;
import com.example.librarymanagement.controller.response.AuthenticationResponse;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.enums.AccountStatusEnum;
import com.example.librarymanagement.model.enums.RoleEnum;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.repository.UserRepository;
import com.example.librarymanagement.service.Imp.AuthenticationService;
import com.example.librarymanagement.service.Imp.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthenticationServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(Member.class))).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.register(request);

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());
        Member savedMember = memberCaptor.getValue();

        Assertions.assertEquals("testUser", savedMember.getUsername());
        Assertions.assertEquals("encodedPassword", savedMember.getPassword());
        Assertions.assertEquals(AccountStatusEnum.ACTIVE, savedMember.getStatus());
        Assertions.assertEquals(RoleEnum.MEMBER, savedMember.getRole());
        Assertions.assertEquals("jwtToken", response.getToken());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testAuthenticate() {
        Member member = new Member();

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");

        when(jwtService.generateToken(any(Member.class))).thenReturn("jwtToken");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(member));

        AuthenticationResponse response = authenticationService.authenticate(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        UsernamePasswordAuthenticationToken authenticationToken = authCaptor.getValue();

        assertEquals("testUser", authenticationToken.getPrincipal());
        assertEquals("testPassword", authenticationToken.getCredentials());
        Assertions.assertEquals("jwtToken", response.getToken());
    }

    @Test
    void resetPassword() {
        String username = "test_user";
        String oldPassword = "old_password";
        String newPassword = "new_password";

        Member existingMember = new Member();
        existingMember.setUsername(username);
        existingMember.setPassword(passwordEncoder.encode(oldPassword));

        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded_new_password");
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, oldPassword);
        ResetPasswordRequest request = new ResetPasswordRequest(authenticationRequest, newPassword);

        authenticationService.resetPassword(request);

        verify(passwordEncoder).encode(newPassword);
        verify(memberRepository).save(existingMember);

        Assertions.assertEquals("encoded_new_password", existingMember.getPassword());
    }
}