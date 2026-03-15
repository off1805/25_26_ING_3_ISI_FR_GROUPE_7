package com.projetTransversalIsi.ue.application.dto;

import java.time.LocalDateTime;

public record UeResponseDTO(
        Long id,
        String libelle,
        String code,
        int credit,
        int volumeHoraireTotal,
        String description,
        String couleur) {
}
