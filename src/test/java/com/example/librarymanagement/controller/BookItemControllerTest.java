package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.BookItem;
import com.example.librarymanagement.model.enums.BookSubjectEnum;
import com.example.librarymanagement.service.BookItemService;
import com.example.librarymanagement.service.Imp.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class BookItemControllerTest {
    @MockBean
    private BookItemService bookItemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private BookItem bookItem1;
    private BookItem bookItem2;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtService jwtService() {
            JwtService jwtService = Mockito.mock(JwtService.class);
            return jwtService;
        }
    }

    @BeforeEach
    void setup() {
        bookItem1 = new BookItem();
        bookItem1.setId(1L);
        bookItem1.setTitle("Book 1");
        bookItem1.setBookSubject(BookSubjectEnum.FICTION);
        bookItem2 = new BookItem();
        bookItem2.setId(2L);
        bookItem2.setTitle("Book 2");
        bookItem2.setBookSubject(BookSubjectEnum.ART);
    }

    @Test
    void getAllBookItems() throws Exception {
        List<BookItem> bookItems = Arrays.asList(
                bookItem1, bookItem2
        );
        Page<BookItem> bookItemsPage = new PageImpl<>(bookItems);

        given(bookItemService.getAllBookItem(any(Pageable.class))).willReturn(bookItemsPage);

        mockMvc.perform(get("/library/books")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Book 1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Book 2"));
    }

    @Test
    void getBookItem() throws Exception {
        given(bookItemService.getBookItem(1L)).willReturn(bookItem1);
        mockMvc.perform(get("/library/books/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book 1"));
    }

    @Test
    @WithMockUser
    void lendBook() throws Exception {
        given(bookItemService.lendBook(1L)).willReturn(bookItem1);

        mockMvc.perform(post("/library/books/lend/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void returnBookItem() throws Exception {
        given(bookItemService.returnBook(1L)).willReturn(bookItem1);

        mockMvc.perform(post("/library/books/return/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void renewBookItem() throws Exception {
        given(bookItemService.renewBook(1L)).willReturn(bookItem1);
        mockMvc.perform(post("/library/books/renew/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void reserveBook() throws Exception {
        given(bookItemService.reserveBook(1L)).willReturn(bookItem1);
        mockMvc.perform(post("/library/books/reserve/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchBook() throws Exception {
        BookSubjectEnum subject = BookSubjectEnum.FICTION;
        int page = 0;
        int pageSize = 10;
        String sort = "title";
        boolean ascending = true;
        Page<BookItem> bookItemsPage = new PageImpl<>(List.of(bookItem1));
        given(bookItemService.searchBookItem(null, null, subject, null,
                PageRequest.of(page, pageSize, Sort.Direction.ASC, sort))).willReturn(bookItemsPage);

        mockMvc.perform(get("/library/books/search")
                        .param("subject", subject.name())
                        .param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sort)
                        .param("ascending", String.valueOf(ascending))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1))) // Verify the number of items in the response
                .andExpect(jsonPath("$.content[0].id").value(1)) // Verify the first item's ID
                .andExpect(jsonPath("$.content[0].title").value("Book 1"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void addBookItem() throws Exception {
        given(bookItemService.addBookItem(any(BookItem.class))).willReturn(bookItem1);

        mockMvc.perform(post("/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItem1)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/library/books/" + bookItem1.getId()));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void addNullBookItem() throws Exception {
        given(bookItemService.addBookItem(any(BookItem.class))).willReturn(null);
        mockMvc.perform(post("/library/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookItem1)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void updateBookItem() throws Exception {
        Long itemId = 1L;

        BookItem updatedBookItem = new BookItem();
        updatedBookItem.setId(itemId);
        updatedBookItem.setTitle("Updated Book");

        given(bookItemService.getBookItem(itemId)).willReturn(bookItem1);
        given(bookItemService.updateBookItem(any(BookItem.class))).willReturn(updatedBookItem);

        mockMvc.perform(put("/library/books/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void updateNonBookItem() throws Exception {
        Long itemId = 1L;

        BookItem updatedBookItem = new BookItem();

        given(bookItemService.getBookItem(itemId)).willReturn(null);

        mockMvc.perform(put("/library/books/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookItem)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void updateNullBookItem() throws Exception {
        Long itemId = 1L;

        BookItem updatedBookItem = new BookItem();
        updatedBookItem.setId(itemId);
        updatedBookItem.setTitle("Updated Book");

        given(bookItemService.getBookItem(itemId)).willReturn(bookItem1);
        given(bookItemService.updateBookItem(any(BookItem.class))).willReturn(null);

        mockMvc.perform(put("/library/books/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookItem)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void removeBookItem() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(delete("/library/books/{itemId}", itemId))
                .andExpect(status().isNoContent());

        verify(bookItemService, times(1)).removeBook(itemId);
    }
}
