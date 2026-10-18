package org.doit.sistticket.member.controller;


import lombok.RequiredArgsConstructor;
import org.doit.sistticket.member.domain.MemberDTO;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberservice;

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }


    @GetMapping("/signup")
    public String signup(){
        return "member/signup";
    }
    @PostMapping("/signup")
    public String signup(MemberDTO memberdto){
        this.memberservice.signup(memberdto);
        return "redirect:/login";
    }
}
