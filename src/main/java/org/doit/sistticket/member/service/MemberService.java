package org.doit.sistticket.member.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.doit.sistticket.member.domain.MemberDTO;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberrepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(MemberDTO memberdto) {
        System.err.println("========== signup 시작 ==========");

        Members member = memberdto.dtoToEntity();

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        
        member.setStatus(1);

        this.memberrepository.save(member);
    }
}
