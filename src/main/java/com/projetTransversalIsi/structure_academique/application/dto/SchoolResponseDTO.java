package com.projetTransversalIsi.structure_academique.application.dto;

import com.projetTransversalIsi.structure_academique.domain.model.School;

public record SchoolResponseDTO(
        Long id,
        String name,
        double latitude,
        double longitude,
        double rayon
) {
    public static SchoolResponseDTO fromDomain(School school) {
        return new SchoolResponseDTO(
                school.getId(),
                school.getName(),
                school.getLatitude(),
                school.getLongitude(),
                school.getRayon()
        );
    }
}
