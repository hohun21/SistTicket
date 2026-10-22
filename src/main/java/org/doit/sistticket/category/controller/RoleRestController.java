package org.doit.sistticket.category.controller;

import org.doit.sistticket.category.dto.RoleRequestDTO;
import org.doit.sistticket.category.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/concert/{concertId}/roles")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    @PostMapping("")
    public ResponseEntity<Long> registerRole(
            @PathVariable("concertId") Long concertId,
            @RequestBody RoleRequestDTO dto) {
        Long roleId = roleService.registerRole(concertId, dto);
        return ResponseEntity.ok(roleId);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleId") Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok().build();
    }
    
 // 배역 수정 API
    @PutMapping("/{roleId}")
    public ResponseEntity<Void> updateRole(
            @PathVariable("roleId") Long roleId,
            @RequestBody RoleRequestDTO dto) {
        roleService.updateRole(roleId, dto);
        return ResponseEntity.ok().build();
    }


}