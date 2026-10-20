package org.doit.sistticket.category.controller;

import java.util.List;

import org.doit.sistticket.category.entity.Role;
import org.doit.sistticket.category.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/concert/{concertId}/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("")
    public String roleManagementPage(@PathVariable("concertId") Long concertId, Model model) {
        List<Role> roles = roleService.getRolesByConcert(concertId);
        model.addAttribute("concertId", concertId);
        model.addAttribute("roles", roles);
        return "concert/roleForm"; // 배역 관리 HTML
    }
}