package com.example.librarymanagement.model.dto;

import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.model.enums.AccountStatusEnum;
import com.example.librarymanagement.model.support.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String username;
    private AccountStatusEnum status;
    private LocalDate dataOfMembership;
    private int totalBooksCheckedOut;
    private LibraryCard card;
    private Person person;
}
