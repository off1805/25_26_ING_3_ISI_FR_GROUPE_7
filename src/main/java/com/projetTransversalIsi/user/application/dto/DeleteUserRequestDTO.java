package com.projetTransversalIsi.user.application.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteUserRequestDTO(
        @NotNull long id
        ) {
}
