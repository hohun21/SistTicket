package org.doit.sistticket.admin.controller;

import lombok.RequiredArgsConstructor;
import org.doit.sistticket.admin.domain.AdminDashBoardDTO;
import org.doit.sistticket.admin.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public AdminDashBoardDTO dashBoardDTO(){
        return adminService.getDashboard();
    }
}
