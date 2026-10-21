package org.doit.sistticket.admin.domain;

import org.doit.sistticket.member.entity.Role;

import java.util.Set;


public record AdminMemberDTO(long memberId,
                             String email,
                             String name,
                             String phone,
                             Set<Role> roles,
                             int status
)
{
}