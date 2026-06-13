package com.projetTransversalIsi.web_application.ap.dto;

public record SeancePresenceEtudiantDTO(
        Long etudiantId,
        String nom,
        String prenom,
        String matricule,
        Boolean present
) {}
