package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.BookReservation;
import com.example.librarymanagement.service.BookReservationService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/library/reservations")
@CrossOrigin
public class BookReservationController {
    private final BookReservationService bookReservationService;

    @GetMapping
    public ResponseEntity<Page<BookReservation>> getAllReservations(@RequestParam("page")int page,
                                                                    @RequestParam("pageSize")int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<BookReservation> reservations = bookReservationService.fetchMemberAllReservations(pageable);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{reservationId}")
    public ResponseEntity<BookReservation> cancelReservation(@PathVariable Long reservationId) {
        BookReservation reservation = bookReservationService.cancelReserve(reservationId);
        log.info("CancelReservation -- reservationId={} UserId={} time={}", reservation.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<BookReservation> getReservationDetails(@PathVariable Long reservationId) {
        BookReservation reservation = bookReservationService.fetchReservationDetails(reservationId);
        log.info("SearchReservation -- reservationId={} UserId={} time={}", reservation.getId(), SecurityUtils.getCurrentUsername(), LocalDateTime.now());
        return ResponseEntity.ok(reservation);
    }
}
