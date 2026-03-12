package com.projetTransversalIsi.classe.application.dto;

import com.projetTransversalIsi.classe.domain.Classe;

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
