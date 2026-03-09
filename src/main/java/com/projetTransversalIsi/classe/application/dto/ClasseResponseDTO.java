package com.projetTransversalIsi.classe.application.dto;


import com.projetTransversalIsi.classe.domain.classe;

public record ClasseResponseDTO(Long id, String nom, int effectif) {
    public static ClasseResponseDTO fromDomain(classe classe) {
        return new ClasseResponseDTO(
                classe.getId(),
                classe.getNom(),
                classe.getEffectif()
        );
    }
}
