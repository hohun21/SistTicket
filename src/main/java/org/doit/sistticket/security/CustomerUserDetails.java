package org.doit.sistticket.security;

import lombok.Getter;
import org.doit.sistticket.member.entity.Members;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomerUserDetails implements UserDetails {

    private final Members member;

    public CustomerUserDetails(Members member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getRoles().stream().map(
                role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        ).toList();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }
    
    @Override
    public boolean isEnabled() {
    	return this.member.getStatus() != 0;
    }
}
