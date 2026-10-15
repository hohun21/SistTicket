package org.doit.sistticket.member.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class MemberController {

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "member/signup";
    }
}
