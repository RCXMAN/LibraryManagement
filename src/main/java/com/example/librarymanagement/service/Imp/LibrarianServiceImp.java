package com.example.librarymanagement.service.Imp;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.repository.LibrarianRepository;
import com.example.librarymanagement.service.LibrarianService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibrarianServiceImp implements LibrarianService {
    private final LibrarianRepository librarianRepository;
    @Override
    public LibraryCard getLibraryCard() {
        return librarianRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_LIBRARIAN))
                .getCard();
    }
}
