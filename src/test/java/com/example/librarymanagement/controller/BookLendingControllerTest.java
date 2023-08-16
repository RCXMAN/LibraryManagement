package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.BookLending;
import com.example.librarymanagement.service.BookLendingService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookLendingController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class BookLendingControllerTest {

    @MockBean
    private BookLendingService bookLendingService;
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
    void getAllLending() throws Exception {
        List<BookLending> lendingList = Arrays.asList(
                new BookLending(),
                new BookLending()
        );
        Page<BookLending> lendingPage = new PageImpl<>(lendingList);

        given(bookLendingService.fetchMemberAllLending(any(Pageable.class))).willReturn(lendingPage);

        mockMvc.perform(get("/library/lending")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("sort", "id")
                        .param("ascending", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lendingList.size())));
    }

}