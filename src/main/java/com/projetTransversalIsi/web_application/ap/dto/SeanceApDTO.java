package com.projetTransversalIsi.web_application.ap.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record SeanceApDTO(
        Long seanceId,
        String libelle,
        String salle,
        LocalDate dateSeance,
        LocalTime heureDebut,
        LocalTime heureFin,
        Long classeId,
        String classeCode,
        String type,
        String enseignantNom,
        String enseignantPrenom,
        String statut,
        Long presenceListId,
        Long nbPresents,
        Long nbAbsents,
        Long nbTotal
) {}
