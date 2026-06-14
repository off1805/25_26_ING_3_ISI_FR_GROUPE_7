package com.projetTransversalIsi.pedagogie.application.dto;

import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;

public record ActivateAnneeScolaireResultDTO(
        AnneeScolaire annee,
        int offresCreees,
        int offresExistantes
) {
}
