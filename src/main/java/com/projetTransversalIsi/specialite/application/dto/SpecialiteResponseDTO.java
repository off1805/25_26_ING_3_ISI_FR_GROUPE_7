package com.projetTransversalIsi.specialite.application.dto;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.classe.application.dto.ClasseResponseDTO;
import java.util.List;
import java.util.stream.Collectors;

public record SpecialiteResponseDTO(
        Long id,
        String code,
        String libelle,
        String description,
        Long niveauId,
        String niveauDescription,
        boolean active,
        List<ClasseResponseDTO> classes
) {
    public static SpecialiteResponseDTO fromDomain(Specialite specialite) {
        return new SpecialiteResponseDTO(
                specialite.getId(),
                specialite.getCode(),
                specialite.getLibelle(),
                specialite.getDescription(),
                specialite.getNiveau() != null ? specialite.getNiveau().getId() : null,
                specialite.getNiveau() != null ? specialite.getNiveau().getDescription() : null,
                specialite.isActive(),
                specialite.getClasses() != null ?
                    specialite.getClasses().stream()
                        .map(ClasseResponseDTO::fromDomain)
                        .collect(Collectors.toList()) : List.of()
        );
    }
}
