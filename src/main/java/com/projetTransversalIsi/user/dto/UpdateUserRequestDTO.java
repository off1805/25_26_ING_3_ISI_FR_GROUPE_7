package com.projetTransversalIsi.user.dto;

import com.projetTransversalIsi.user.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String role,
        @NotNull UserStatus status) {
}
