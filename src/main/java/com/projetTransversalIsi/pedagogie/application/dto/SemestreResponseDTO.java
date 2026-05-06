package com.projetTransversalIsi.pedagogie.application.dto;

import java.time.LocalDate;

public record SemestreResponseDTO(
        Long id,
        Integer numero,
        String libelle,
        LocalDate dateDebut,
        LocalDate dateFin,
        Long anneeScolaireId,
        Long niveauId
) {
}