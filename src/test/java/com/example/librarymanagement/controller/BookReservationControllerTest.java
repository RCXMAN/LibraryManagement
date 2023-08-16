package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.BookReservation;
import com.example.librarymanagement.service.BookReservationService;
import com.example.librarymanagement.service.Imp.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class BookReservationControllerTest {

    @MockBean
    private BookReservationService bookReservationService;
    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtService jwtService() {
            JwtService jwtService = Mockito.mock(JwtService.class);
            return jwtService;
        }
    }

    @Test
    void getAllReservations() throws Exception {
        List<BookReservation> reservationList = Arrays.asList(
                new BookReservation(),
                new BookReservation()
        );
        Page<BookReservation> reservationPage = new PageImpl<>(reservationList);

        given(bookReservationService.fetchMemberAllReservations(any(Pageable.class))).willReturn(reservationPage);

        mockMvc.perform(get("/library/reservations")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(reservationList.size())));
    }

    @Test
    @WithMockUser
    void cancelReservation() throws Exception {
        Long reservationId = 1L;
        BookReservation canceledReservation = new BookReservation();
        canceledReservation.setId(reservationId);

        given(bookReservationService.cancelReserve(reservationId)).willReturn(canceledReservation);

        mockMvc.perform(post("/library/reservations/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getReservationDetails() throws Exception {
        Long reservationId = 1L;
        BookReservation reservation = new BookReservation();
        reservation.setId(reservationId);

        given(bookReservationService.fetchReservationDetails(reservationId)).willReturn(reservation);

        mockMvc.perform(get("/library/reservations/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}