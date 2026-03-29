package com.projetTransversalIsi.student.application.dto;

public record EnrollStudentResponseDTO(
        Long userId,
        String email,
        String nom,
        String prenom,
        String matricule,
        Long classeId,
        boolean created) {
}
