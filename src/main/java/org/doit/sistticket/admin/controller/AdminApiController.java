package org.doit.sistticket.admin.controller;

import lombok.RequiredArgsConstructor;
import org.doit.sistticket.admin.domain.AdminDashBoardDTO;
import org.doit.sistticket.admin.domain.AdminMemberDTO;
import org.doit.sistticket.admin.domain.AdminMemberUpdateDTO;
import org.doit.sistticket.admin.service.AdminService;
import org.doit.sistticket.member.domain.MemberDTO;
import org.doit.sistticket.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;
    private final MemberService memberService;

    @GetMapping("/dashboard")
    public AdminDashBoardDTO dashBoardDTO(){
        return adminService.getDashboard();
    }

    @GetMapping("/members")
    public List<AdminMemberDTO> allMembers() {
        List<AdminMemberDTO> mdto = this.memberService.findAllAdminMembers();
        return mdto;
    }

    @PatchMapping("/members/{memberId}")
    public ResponseEntity<Void> updateMember(
            @PathVariable("memberId") Long memberId,
            @RequestBody AdminMemberUpdateDTO dto){
        memberService.updateMember(memberId,dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Void> checkAdmin(){
        return ResponseEntity.noContent().build();
    }
}
