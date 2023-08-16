package com.example.librarymanagement.controller;

import com.example.librarymanagement.service.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("library/librarian")
@CrossOrigin
public class LibrarianController {
    private final LibrarianService librarianService;
}
