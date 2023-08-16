package com.example.librarymanagement.service;

import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.dto.MemberDto;

public interface MemberService extends UserService{
    int getTotalBooksCheckout(String username);
    boolean cardIsActive(String username);
    void incrementTotalBooksCheckedOut(String username);
    void decrementTotalBooksCheckedOut(String memberId);
    Member getMember(String username);
    MemberDto getProfile();
    MemberDto editProfile(MemberDto memberDto);
    void blockMember(String username);
    void unBlockMember(String username);
}
