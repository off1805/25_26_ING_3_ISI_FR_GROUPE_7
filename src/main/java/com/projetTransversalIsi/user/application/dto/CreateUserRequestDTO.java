package com.projetTransversalIsi.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUserRequestDTO(
        @NotBlank @Email String email,
        @NotBlank @Size(min =  8) String password,
        @NotBlank String idRole, 
        @NotEmpty Set<String> idPermissions) {
}
