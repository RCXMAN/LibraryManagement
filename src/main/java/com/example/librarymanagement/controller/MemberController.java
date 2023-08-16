package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.dto.MemberDto;
import com.example.librarymanagement.service.MemberService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/library/members")
@CrossOrigin
public class MemberController {
    private final MemberService memberService;

    private final ModelMapper mapper = new ModelMapper();

    @GetMapping("/profile")
    public ResponseEntity<MemberDto> getProfile() {
        MemberDto memberDto = memberService.getProfile();
        return ResponseEntity.ok(memberDto);
    }

    @PutMapping("/editProfile")
    public ResponseEntity<MemberDto> editProfile(@RequestBody MemberDto updateMemberDto) {
        MemberDto memberDto = memberService.editProfile(updateMemberDto);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/libraryCard")
    public ResponseEntity<LibraryCard> getLibraryCard() {
        LibraryCard libraryCard = memberService.getLibraryCard();
        return ResponseEntity.ok(libraryCard);
    }

    @GetMapping("/total-books-checkout")
    public ResponseEntity<Integer> getTotalBooksCheckedOut() {
        int totalBooksCheckedOut = memberService.getTotalBooksCheckout(SecurityUtils.getCurrentUsername());
        return ResponseEntity.ok(totalBooksCheckedOut);
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @GetMapping("/{username}")
    public ResponseEntity<MemberDto> getSpecificMemberDetails(@PathVariable String username) {
        Member member = memberService.getMember(username);
        MemberDto memberDto = mapper.map(member, MemberDto.class);
        log.info("SearchMember -- username={} UserId={}", username, SecurityUtils.getCurrentUsername());
        return ResponseEntity.ok(memberDto);
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PutMapping("/{username}/block")
    public ResponseEntity<Void> blockMember(@PathVariable String username) {
        memberService.blockMember(username);
        log.info("BlockMember -- MemberId={} UserId={}", username, SecurityUtils.getCurrentUsername());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PutMapping("/{username}/unblock")
    public ResponseEntity<Void> unblockMember(@PathVariable String username) {
        memberService.unBlockMember(username);
        log.info("UnblockMember -- MemberId={} UserId={}", username, SecurityUtils.getCurrentUsername());
        return ResponseEntity.noContent().build();
    }
}
