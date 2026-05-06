package com.projetTransversalIsi.justificatif.application.dto;

import java.time.LocalDate;

public record SoumettreJustificatifDTO(
        Long etudiantId,
        Long seanceId,
        String motif,
        LocalDate dateAbsence
) {}
