package org.doit.sistticket.member.service;

import java.util.List;

import org.doit.sistticket.admin.domain.AdminMemberDTO;
import org.doit.sistticket.admin.domain.AdminMemberUpdateDTO;
import org.doit.sistticket.member.domain.MemberRequestDTO;
import org.doit.sistticket.member.domain.MemberResponseDTO;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberrepository;
    private final PasswordEncoder passwordEncoder;

    // 💡 회원 가입 (여기에 이미 status(1)과 비밀번호 암호화가 잘 적용되어 있습니다!)
    @Transactional
    public MemberResponseDTO register(MemberRequestDTO request){
        Members member = Members.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .status(1) 
                .build();
        Members savedMember = memberrepository.save(member);
        return MemberResponseDTO.from(savedMember);
    }

    @Transactional
    public void updateMember(Long memberId, AdminMemberUpdateDTO dto){
        Members member = memberrepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원을 찾을 수 없습니다.")
                        );
        // 비밀번호가 넘어왔을 때만 변경
        if(dto.password() != null && !dto.password().isBlank()){
            member.setPassword(passwordEncoder.encode(dto.password()));
        }
//        if (dto.roles() != null && !dto.roles().isEmpty()) {
            member.setRoles(dto.roles());
//        }
        member.setStatus(dto.status());
        memberrepository.save(member);
    }

    // 💡 조원분이 추가한 관리자용 전체 회원 조회 메서드
    public List<AdminMemberDTO> findAllAdminMembers() {
        return memberrepository.findAll()
                .stream()
                .map(member -> new AdminMemberDTO(
                        member.getMemberId(),
                        member.getEmail(),
                        member.getName(),
                        member.getPhone(),
                        member.getRoles(),
                        member.getStatus()
                ))
                .toList();
    }
}