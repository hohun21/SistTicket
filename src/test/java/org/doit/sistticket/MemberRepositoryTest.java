package org.doit.sistticket;

import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.entity.Role;
import org.doit.sistticket.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void insertAdmin(){
        Members member = Members.builder()
                .email("admin@sist.com")
                .password(passwordEncoder.encode("12341234"))
                .name("관리자")
                .roles(Set.of(Role.ADMIN))
                .phone("010-9341-6553")
                .status(0)
                .build();
        this.memberRepository.save(member);
    }
}
