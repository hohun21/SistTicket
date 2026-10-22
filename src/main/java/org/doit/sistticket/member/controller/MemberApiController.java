package org.doit.sistticket.member.controller;

import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.doit.sistticket.member.domain.FindPasswordRequestDTO;
import org.doit.sistticket.member.domain.MemberRequestDTO;
import org.doit.sistticket.member.domain.MemberResponseDTO;
import org.doit.sistticket.member.service.MemberService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberservice;

    // 💡 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDTO> register(@Valid @RequestBody
                                                      MemberRequestDTO request){
        MemberResponseDTO response = memberservice.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // 💡 아이디 찾기
    @GetMapping("/find-id")
    public ResponseEntity<String> findId(
            @RequestParam("name") String name,
            @RequestParam("phone") String phone
    ){
        String email = this.memberservice.findIdByEmailAndPhone(name, phone);
        if(email == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("일치하는 회원 정보를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(email);
    }

    // 💡 비밀번호 찾기 (임시 비밀번호 발급)
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(
            @Valid @RequestBody FindPasswordRequestDTO request){
        try {
            String temporaryPassword = memberservice.resetPasswordWithTemporary(request);
            return ResponseEntity.ok(temporaryPassword);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // email 중복확인
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(
            @RequestParam("email") String email
    ){
        boolean available = memberservice.isEmailAvailable(email);

        return ResponseEntity.ok(
                Map.of("available", available)
        );
    }
}