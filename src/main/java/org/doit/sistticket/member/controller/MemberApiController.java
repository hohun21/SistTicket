package org.doit.sistticket.member.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.doit.sistticket.member.domain.MemberRequestDTO;
import org.doit.sistticket.member.domain.MemberResponseDTO;
import org.doit.sistticket.member.service.MemberService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberservice;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDTO> register(@Valid @RequestBody
                                                      MemberRequestDTO request){
        MemberResponseDTO response = memberservice.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
    
}
