package com.example.librarymanagement.service.Imp;

import com.example.librarymanagement.constant.ExceptionConstants;
import com.example.librarymanagement.exception.NoSuchSourceException;
import com.example.librarymanagement.model.LibraryCard;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.model.dto.MemberDto;
import com.example.librarymanagement.model.enums.AccountStatusEnum;
import com.example.librarymanagement.repository.MemberRepository;
import com.example.librarymanagement.service.MemberService;
import com.example.librarymanagement.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberServiceImp implements MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper mapper = new ModelMapper();
    @Override
    public int getTotalBooksCheckout(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER))
                .getTotalBooksCheckedOut();
    }
    @Override
    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
    }
    @Override
    public MemberDto getProfile() {
         return memberRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .map(memberEntity -> mapper.map(memberEntity, MemberDto.class))
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
    }
    @Override
    public MemberDto editProfile(MemberDto memberDto) {
        Member member = memberRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
        member.setStatus(memberDto.getStatus());
        member.setTotalBooksCheckedOut(memberDto.getTotalBooksCheckedOut());

        member.getPerson().setName(memberDto.getPerson().getName());
        member.getPerson().setEmail(memberDto.getPerson().getEmail());
        member.getPerson().setPhone(memberDto.getPerson().getPhone());

        member.getPerson().getAddress().setStreet(memberDto.getPerson().getAddress().getStreet());
        member.getPerson().getAddress().setCity(memberDto.getPerson().getAddress().getCity());
        member.getPerson().getAddress().setState(memberDto.getPerson().getAddress().getState());
        member.getPerson().getAddress().setZipCode(memberDto.getPerson().getAddress().getZipCode());
        member.getPerson().getAddress().setCountry(memberDto.getPerson().getAddress().getCountry());

        Member updateMember =  memberRepository.save(member);

        return mapper.map(updateMember, MemberDto.class);
    }
    @Override
    public void blockMember(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
        member.setStatus(AccountStatusEnum.BLACKLISTED);
        memberRepository.save(member);
    }
    @Override
    public void unBlockMember(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
        member.setStatus(AccountStatusEnum.ACTIVE);
        memberRepository.save(member);
    }
    @Override
    public void incrementTotalBooksCheckedOut(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
        member.setTotalBooksCheckedOut(member.getTotalBooksCheckedOut() + 1);
        memberRepository.save(member);
    }
    @Override
    public void decrementTotalBooksCheckedOut(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER));
        member.setTotalBooksCheckedOut(member.getTotalBooksCheckedOut() - 1);
        memberRepository.save(member);
    }
    @Override
    public boolean cardIsActive(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER))
                .getCard()
                .isActive();
    }
    @Override
    public LibraryCard getLibraryCard() {
        return memberRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new NoSuchSourceException(ExceptionConstants.NO_SUCH_MEMBER))
                .getCard();
    }
}
