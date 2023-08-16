package com.example.librarymanagement.service;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.dto.MemberDto;
import com.example.librarymanagement.model.enums.AccountStatusEnum;
import com.example.librarymanagement.model.support.Address;
import com.example.librarymanagement.model.support.Person;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.service.Imp.MemberServiceImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WithMockUser(username = MemberServiceImpTest.TEST_USERNAME)
class MemberServiceImpTest {

    private final ModelMapper mapper = new ModelMapper();
    public static final String TEST_USERNAME = "joey";
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberServiceImp memberServiceImp;

    @Test
    void getTotalBooksCheckout() {
        int totalBooksCheckedOut = 5;
        Member member = new Member();
        member.setTotalBooksCheckedOut(totalBooksCheckedOut);

        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));

        int result = memberServiceImp.getTotalBooksCheckout(TEST_USERNAME);

        assertEquals(totalBooksCheckedOut, result);
    }

    @Test
    void getMember() {
        Member member = new Member();
        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));
        Assertions.assertEquals(member, memberServiceImp.getMember(TEST_USERNAME));
    }

    @Test
    void getMember_ThrowsNoSuchSourceException() {
        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> memberServiceImp.getMember(TEST_USERNAME))
                .isInstanceOf(NoSuchSourceException.class)
                .hasMessageContaining(ExceptionConstants.NO_SUCH_MEMBER);
    }

    @Test
    void getProfile() {
        Member member = new Member();
        member.setUsername(TEST_USERNAME);
        member.setStatus(AccountStatusEnum.ACTIVE);

        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john.doe@example.com");
        person.setPhone("1234567890");

        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setZipCode("10001");
        address.setCountry("USA");

        person.setAddress(address);
        member.setPerson(person);

        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));

        MemberDto expectedMemberDto = mapper.map(member, MemberDto.class);

        MemberDto result = memberServiceImp.getProfile();

        Assertions.assertEquals(expectedMemberDto, result);
    }

    @Test
    void editProfile() {
        MemberDto memberDto = new MemberDto();
        memberDto.setUsername(TEST_USERNAME);
        memberDto.setStatus(AccountStatusEnum.ACTIVE);
        memberDto.setTotalBooksCheckedOut(5);
        LibraryCard libraryCard = new LibraryCard();
        memberDto.setCard(libraryCard);
        Person personDto = new Person();
        personDto.setName("New Name");
        personDto.setEmail("new@example.com");
        personDto.setPhone("1234567890");
        Address addressDto = new Address();
        addressDto.setStreet("New Street");
        addressDto.setCity("New City");
        addressDto.setState("New State");
        addressDto.setZipCode("12345");
        addressDto.setCountry("New Country");

        personDto.setAddress(addressDto);
        memberDto.setPerson(personDto);

        Member existingMember = new Member();
        existingMember.setUsername(TEST_USERNAME);

        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        MemberDto updatedMemberDto = memberServiceImp.editProfile(memberDto);

        Assertions.assertEquals(memberDto, updatedMemberDto);
    }

    @Test
    void blockMember() {
        Member member = new Member();
        member.setUsername(TEST_USERNAME);
        member.setStatus(AccountStatusEnum.ACTIVE);

        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));

        memberServiceImp.blockMember(TEST_USERNAME);

        verify(memberRepository, times(1)).findByUsername(TEST_USERNAME);
        Assertions.assertEquals(AccountStatusEnum.BLACKLISTED, member.getStatus());
    }

    @Test
    void unBlockMember() {
        Member member = new Member();
        member.setUsername(TEST_USERNAME);
        member.setStatus(AccountStatusEnum.BLACKLISTED);

        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));

        memberServiceImp.unBlockMember(TEST_USERNAME);

        verify(memberRepository, times(1)).findByUsername(TEST_USERNAME);
        Assertions.assertEquals(AccountStatusEnum.ACTIVE, member.getStatus());
    }

    @Test
    void incrementTotalBooksCheckedOut() {
        Member member = new Member();
        member.setUsername(TEST_USERNAME);
        member.setTotalBooksCheckedOut(3);
        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));
        memberServiceImp.incrementTotalBooksCheckedOut(TEST_USERNAME);
        Assertions.assertEquals(4, member.getTotalBooksCheckedOut());
    }

    @Test
    void decrementTotalBooksCheckedOut() {
        Member member = new Member();
        member.setUsername(TEST_USERNAME);
        member.setTotalBooksCheckedOut(3);
        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));
        memberServiceImp.decrementTotalBooksCheckedOut(TEST_USERNAME);
        Assertions.assertEquals(2, member.getTotalBooksCheckedOut());
    }

    @Test
    void cardIsActive() {
        Member member = new Member();
        member.setUsername(TEST_USERNAME);
        LibraryCard libraryCard = new LibraryCard();
        libraryCard.setActive(true);
        member.setCard(libraryCard);

        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));
        Assertions.assertEquals(true, memberServiceImp.cardIsActive(TEST_USERNAME));
    }

    @Test
    void getLibraryCard() {
        Member member = new Member();
        LibraryCard libraryCard = new LibraryCard();
        libraryCard.setCardNumber("111");
        libraryCard.setIssued(new Date());
        libraryCard.setActive(true);
        member.setCard(libraryCard);

        when(memberRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(member));

        Assertions.assertEquals(libraryCard, memberServiceImp.getLibraryCard());
    }
}