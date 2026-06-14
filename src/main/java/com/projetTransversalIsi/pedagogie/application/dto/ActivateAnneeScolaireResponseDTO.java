package com.projetTransversalIsi.pedagogie.application.dto;

public record ActivateAnneeScolaireResponseDTO(
        CreateAnneeScolaireResponseDTO annee,
        int offresCreees,
        int offresExistantes
) {
}
