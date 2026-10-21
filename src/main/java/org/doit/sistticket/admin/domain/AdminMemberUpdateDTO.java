package org.doit.sistticket.admin.domain;

import org.doit.sistticket.member.entity.Role;

import java.util.Set;

public record AdminMemberUpdateDTO(
        String password,
        Set<Role> roles,
        Integer status
) {
}
