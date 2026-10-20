package org.doit.sistticket.member.domain;


import lombok.*;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.entity.Role;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private long memberId;
    private String email;
    private String password;
    private String name;
    private String phone;
    public static MemberDTO entityToDTO(Members entity) {
        return MemberDTO.builder()
                .memberId(entity.getMemberId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .phone(entity.getPhone())
                .build();
    }
    public Members dtoToEntity() {
        return Members.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .phone(this.phone)
                .roles(Set.of(Role.USER))
                .status(0)
                .build();
    }
}
