package com.projetTransversalIsi.structure_academique.application.dto;

import com.projetTransversalIsi.structure_academique.domain.model.Classe;

public record ClasseResponseDTO(
        Long id,
        String code,
        String description,
        Long specialiteId
) {
    public static ClasseResponseDTO fromDomain(Classe classe) {
        return new ClasseResponseDTO(
                classe.getId(),
                classe.getCode(),
                classe.getDescription(),
                classe.getSpecialite() != null ? classe.getSpecialite().getId() : null
        );
    }
}
