package com.projetTransversalIsi.web_application.ap.dto;

import java.time.LocalDate;

public record AbsenceDetailLigneDTO(
        Long seanceId,
        LocalDate date,
        String heureDebut,
        String heureFin,
        String salle,
        String ueLibelle,
        String ueCode,
        boolean present,
        String justificatifStatut
) {}
