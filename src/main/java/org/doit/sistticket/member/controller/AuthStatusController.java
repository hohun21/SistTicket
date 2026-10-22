package org.doit.sistticket.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.doit.sistticket.security.CustomerUserDetails;

@RestController
public class AuthStatusController {

    @GetMapping("/api/member/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Logged In");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        
        String realName = userDetails.getMember().getName(); 

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", authentication.getName());
        userInfo.put("role", isAdmin ? "ROLE_ADMIN" : "ROLE_USER");
        userInfo.put("name", realName);

        return ResponseEntity.ok(userInfo);
    }
}