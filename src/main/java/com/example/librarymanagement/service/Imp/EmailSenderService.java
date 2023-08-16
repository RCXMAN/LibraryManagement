package com.example.librarymanagement.service.Imp;

import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmailToUser(User user,
                                String subject,
                                String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        String toEmail = user.getPerson().getEmail();
        message.setFrom(LibraryConstants.INFO_EMAIL);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        log.info("Email sent to user successfully.");
    }
}
