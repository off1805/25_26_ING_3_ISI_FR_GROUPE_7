package com.projetTransversalIsi.web_application.ap.dto;

public record AbsenceEtudiantDTO(
        Long etudiantId,
        String nom,
        String prenom,
        String matricule,
        String photoUrl,
        Long classeId,
        String classeCode,
        Long totalEnregistrements,
        Long totalAbsences,
        Long nbJustifiees,
        Long nbEnAttente
) {}
