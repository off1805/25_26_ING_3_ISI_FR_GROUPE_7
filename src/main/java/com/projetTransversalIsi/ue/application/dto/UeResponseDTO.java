package com.projetTransversalIsi.ue.application.dto;

import java.util.Set;

public record UeResponseDTO(
        Long id,
        String libelle,
        String code,
        int credit,
        int volumeHoraireTotal,
        String description,
        String couleur,
        Long specialiteId,
        Set<Long> enseignantIds) {
}
