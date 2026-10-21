package org.doit.sistticket.member.domain;

import lombok.Builder;
import lombok.Getter;
import org.doit.sistticket.member.entity.Members;

@Getter
@Builder
public class MemberResponseDTO {

    private long memberId;
    private String email;
    private String name;
    private String phone;

    public static MemberResponseDTO from(Members entity){
        return MemberResponseDTO.builder()
                .memberId(entity.getMemberId())
                .email(entity.getEmail())
                .name(entity.getName())
                .phone(entity.getPhone())
                .build();
    }
}
