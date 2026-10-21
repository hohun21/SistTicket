package org.doit.sistticket.member.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindPasswordRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String name,
        @NotBlank String phone
) {
}
