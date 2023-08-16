package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import com.example.librarymanagement.service.BookItemService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/library/books")
@CrossOrigin
public class BookItemController {
    private final BookItemService bookItemService;

    @GetMapping
    public ResponseEntity<Page<BookItem>> getAllBookItems(@RequestParam("page")int page, @RequestParam("pageSize")int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BookItem> bookItemPage = bookItemService.getAllBookItem(pageable);
        return ResponseEntity.ok(bookItemPage);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<BookItem> getBookItem(@PathVariable Long itemId) {
        BookItem bookItem = bookItemService.getBookItem(itemId);
        return ResponseEntity.ok(bookItem);
    }

    @PostMapping("/lend/{itemId}")
    public ResponseEntity<BookItem> lendBook(@PathVariable Long itemId) {
        BookItem lendBook = bookItemService.lendBook(itemId);
        log.info("LendBook -- bookId={} UserId={} time={}", lendBook.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());
        return ResponseEntity.ok(lendBook);
    }

    @PostMapping("/return/{itemId}")
    public ResponseEntity<BookItem> returnBookItem(@PathVariable Long itemId) {
        BookItem returnedBook = bookItemService.returnBook(itemId);
        log.info("ReturnBook -- bookId={} UserId={} time={}", returnedBook.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());
        return ResponseEntity.ok(returnedBook);
    }

    @PostMapping("/renew/{itemId}")
    public ResponseEntity<BookItem> renewBookItem(@PathVariable Long itemId) {
        BookItem renewedBook = bookItemService.renewBook(itemId);
        log.info("RenewBook -- bookId={} UserId={} time={}", renewedBook.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());
        return ResponseEntity.ok(renewedBook);
    }

    @PostMapping("/reserve/{itemId}")
    public ResponseEntity<BookItem> reserveBook(@PathVariable Long itemId) {
        BookItem reservedBook = bookItemService.reserveBook(itemId);
        log.info("ReserveBook -- bookId={} UserId={} time={}", reservedBook.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());
        return ResponseEntity.ok(reservedBook);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookItem>> searchBook(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) BookSubjectEnum subject,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publicationDate,
            @RequestParam("page")int page, @RequestParam("pageSize")int pageSize,
            @RequestParam("sort")String sort,
            @RequestParam("ascending") boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, direction, sort);
        Page<BookItem> books = bookItemService.searchBookItem(title, authorName, subject, publicationDate, pageable);
        return ResponseEntity.ok(books);
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PostMapping
    public ResponseEntity<BookItem> addBookItem(@RequestBody BookItem bookItem) {
        BookItem createdBookItem = bookItemService.addBookItem(bookItem);

        if (createdBookItem == null) {
            return ResponseEntity.noContent().build();
        }

        log.info("CreateBook -- bookId={} UserId={} time={}", createdBookItem.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{itemId}")
                .buildAndExpand(createdBookItem.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PutMapping("/{itemId}")
    public ResponseEntity<BookItem> updateBookItem(@PathVariable Long itemId , @RequestBody BookItem bookItem) {
        BookItem existingBookItem = bookItemService.getBookItem(itemId);
        if (existingBookItem == null) {
            return ResponseEntity.notFound().build();
        }

        BookItem updatedBookItem = bookItemService.updateBookItem(bookItem);

        if (updatedBookItem == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("UpdateBook -- bookId={} UserId={} time={}", updatedBookItem.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());

        return ResponseEntity.ok(updatedBookItem);
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeBookItem(@PathVariable Long itemId) {
        bookItemService.removeBook(itemId);
        log.info("RemoveBook -- bookId={} UserId={} time={}", itemId, SecurityUtils.getCurrentUsername(), LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }
}

