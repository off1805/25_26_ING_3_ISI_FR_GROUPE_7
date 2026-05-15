package com.projetTransversalIsi.justificatif.application.dto;

import java.time.LocalDate;
import java.util.List;

// etudiantId est résolu par le controller depuis le principal Spring Security — non exposé au client.
public record SoumettreJustificatifDTO(
        Long etudiantId,
        List<Long> seanceIds,
        String motif,
        String message,
        LocalDate dateAbsence
) {}
