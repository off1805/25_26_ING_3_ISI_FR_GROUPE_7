package com.projetTransversalIsi.emploi_temps.application.dto;

import java.time.LocalDate;

public record AbsenceEtudiantDTO(
        Long presenceRowId,
        Long presenceListId,
        Long seanceId,
        LocalDate dateSeance,
        String matiere,
        int minutesAbsence,
        int minutesTotales,
        // NON_JUSTIFIEE | EN_COURS | JUSTIFIEE | REJETEE
        String statutJustificatif,
        Long justificatifId,
        // Commentaire de l'AP (présent si JUSTIFIEE ou REJETEE)
        String commentaireAP
) {}
