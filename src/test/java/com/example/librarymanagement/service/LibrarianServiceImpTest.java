package com.example.librarymanagement.service;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.Librarian;
import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.repository.LibrarianRepository;
import com.example.librarymanagement.service.Imp.LibrarianServiceImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WithMockUser(username = "joey")
class LibrarianServiceImpTest {

    @Mock
    private LibrarianRepository librarianRepository;
    @InjectMocks
    private LibrarianServiceImp librarianServiceImp;

    @Test
    void getLibraryCard() {
        Librarian simpleMember = new Librarian();
        simpleMember.setUsername("joey");
        LibraryCard libraryCard = new LibraryCard();
        simpleMember.setCard(libraryCard);

        when(librarianRepository.findByUsername("joey")).thenReturn(Optional.of(simpleMember));

        LibraryCard resultLibraryCard = librarianServiceImp.getLibraryCard();

        Assertions.assertEquals(libraryCard, resultLibraryCard);
    }

    @Test
    void testGetLibraryCard_ThrowsNoSuchSourceException() {
        when(librarianRepository.findByUsername("joey")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> librarianServiceImp.getLibraryCard())
                .isInstanceOf(NoSuchSourceException.class)
                .hasMessageContaining(ExceptionConstants.NO_SUCH_LIBRARIAN);
    }
}