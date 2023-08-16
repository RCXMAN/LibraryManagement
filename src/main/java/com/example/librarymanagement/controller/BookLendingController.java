package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.service.BookLendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/library/lending")
@CrossOrigin(origins ="http://localhost:3000")
public class BookLendingController {

    private final BookLendingService bookLendingService;

    @GetMapping
    public ResponseEntity<Page<BookLending>> getAllLending(@RequestParam("page")int page,
                                                           @RequestParam("pageSize")int pageSize,
                                                           @RequestParam("sort")String sort,
                                                           @RequestParam("ascending") boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, direction, sort);
        Page<BookLending> bookLending = bookLendingService.fetchMemberAllLending(pageable);
        return ResponseEntity.ok(bookLending);
    }
}
