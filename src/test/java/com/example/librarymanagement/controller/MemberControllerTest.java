package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.dto.MemberDto;
import com.example.librarymanagement.model.enums.RoleEnum;
import com.example.librarymanagement.service.Imp.JwtService;
import com.example.librarymanagement.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class MemberControllerTest {
    @MockBean
    private MemberService memberService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtService jwtService() {
            JwtService jwtService = Mockito.mock(JwtService.class);
            return jwtService;
        }
    }

    @Test
    void getProfile() throws Exception {
        MemberDto memberDto = new MemberDto();
        memberDto.setUsername("test_user");

        given(memberService.getProfile()).willReturn(memberDto);

        mockMvc.perform(get("/library/members/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(memberDto.getUsername()));
    }

    @Test
    void editProfile() throws Exception {
        MemberDto updateMemberDto = new MemberDto();
        updateMemberDto.setUsername("test_user");

        given(memberService.editProfile(any(MemberDto.class))).willReturn(updateMemberDto);

        mockMvc.perform(put("/library/members/editProfile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updateMemberDto.getUsername()));
    }

    @Test
    void getLibraryCard() throws Exception {
        LibraryCard libraryCard = new LibraryCard();
        libraryCard.setCardNumber("1234567890");

        given(memberService.getLibraryCard()).willReturn(libraryCard);

        mockMvc.perform(get("/library/members/libraryCard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value(libraryCard.getCardNumber()));
    }

    @Test
    @WithMockUser
    void getTotalBooksCheckedOut() throws Exception {
        int totalBooksCheckedOut = 5;

        given(memberService.getTotalBooksCheckout(anyString())).willReturn(totalBooksCheckedOut);

        mockMvc.perform(get("/library/members/total-books-checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(totalBooksCheckedOut));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void getSpecificMemberDetails() throws Exception {
        String username = "test_user";
        Member member = new Member();
        member.setRole(RoleEnum.MEMBER);
        member.setUsername(username);

        given(memberService.getMember(username)).willReturn(member);

        mockMvc.perform(get("/library/members/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void blockMember() throws Exception {
        String username = "test_user";

        mockMvc.perform(put("/library/members/{username}/block", username))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).blockMember(username);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void unblockMember() throws Exception {
        String username = "test_user";

        mockMvc.perform(put("/library/members/{username}/unblock", username))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).unBlockMember(username);
    }
}