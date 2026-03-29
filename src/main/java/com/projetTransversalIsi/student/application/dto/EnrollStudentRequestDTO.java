package com.projetTransversalIsi.student.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnrollStudentRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank String matricule,
        @NotBlank String numeroTelephone,
        @NotNull Long classeId) {
}
