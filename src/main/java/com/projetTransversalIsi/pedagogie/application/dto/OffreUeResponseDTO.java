package com.projetTransversalIsi.pedagogie.application.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record OffreUeResponseDTO(
        Long id,
        Long ueId,
        Long anneeScolaireId,
        String libelle,
        String code,
        int credit,
        int volumeHoraireTotal,
        String description,
        String couleur,
        Integer semestre,
        Long specialiteId,
        Set<Long> enseignantIds,
        LocalDateTime createdAt) {
}
