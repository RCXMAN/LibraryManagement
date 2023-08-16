package com.example.librarymanagement.service;

import com.example.librarymanagement.constant.LibraryConstants;
import com.example.librarymanagement.model.User;
import com.example.librarymanagement.model.support.Person;
import com.example.librarymanagement.service.Imp.EmailSenderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class EmailSenderServiceTest {
    @Mock
    private JavaMailSender emailSender;
    @InjectMocks
    private EmailSenderService emailSenderService;

    @Test
    void sendEmailToUser() {
        User user = new User();
        Person person = new Person();
        person.setEmail("test@test.com");
        user.setPerson(person);

        String subject = "Test Subject";
        String body = "Test Body";

        emailSenderService.sendEmailToUser(user, subject, body);

        verify(emailSender).send(any(SimpleMailMessage.class));

        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender).send(mailMessageCaptor.capture());

        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals("test@test.com", sentMessage.getTo()[0]);
        Assertions.assertEquals(LibraryConstants.INFO_EMAIL, sentMessage.getFrom());
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }
}