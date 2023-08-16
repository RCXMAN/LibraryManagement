package com.example.librarymanagement.service.Imp;


import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.controller.request.AuthenticationRequest;
import com.example.librarymanagement.controller.request.RegisterRequest;
import com.example.librarymanagement.controller.request.ResetPasswordRequest;
import com.example.librarymanagement.controller.response.AuthenticationResponse;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.User;
import com.example.librarymanagement.model.enums.AccountStatusEnum;
import com.example.librarymanagement.model.enums.RoleEnum;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthenticationResponse register(RegisterRequest request) {
        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .person(request.getPerson())
                .status(AccountStatusEnum.ACTIVE)
                .role(RoleEnum.MEMBER)
                .card(new LibraryCard())
                .dataOfMembership(LocalDate.now())
                .totalBooksCheckedOut(LibraryConstants.MAX_BOOK_LIMIT)
                .build();
        memberRepository.save(member);

        String jwtToken = jwtService.generateToken(member);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    public void resetPassword(ResetPasswordRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getAuthenticationRequest().getUsername(),
                        request.getAuthenticationRequest().getPassword())
        );
        Member member = memberRepository.findByUsername(request.getAuthenticationRequest().getUsername())
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.setPassword(encodedPassword);
        memberRepository.save(member);
    }
}
