package org.doit.sistticket.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {
    private String roleName;    // 배역 이름 (예: 엘리자벳, 토텐)
    private String description; // 배역 설명
}