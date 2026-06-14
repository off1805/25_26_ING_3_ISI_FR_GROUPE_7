package com.projetTransversalIsi.web_application.ap.dto;

import java.time.LocalDate;

public record ClasseHistoryArchiveDTO(
        Long etudiantId,
        String nom,
        String prenom,
        String matricule,
        String classeCode,
        LocalDate dateDebut,
        LocalDate dateFin
) {}
