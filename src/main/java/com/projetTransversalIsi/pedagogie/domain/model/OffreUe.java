package com.projetTransversalIsi.pedagogie.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Représente l'offre d'une UE pour une année scolaire donnée.
 * Les champs de base sont recopiés depuis l'UE au moment de la création
 * afin de conserver un historique fidèle, même si l'UE évolue par la suite.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreUe {

    private Long id;

    // --- Référence à l'UE source et à l'année scolaire ---
    private Long ueId;
    private Long anneeScolaireId;

    // --- Champs recopiés depuis l'UE ---
    private String libelle;
    private String code;
    private int credit;
    private int volumeHoraireTotal;
    private String description;
    private String couleur;
    private Long specialiteId;
    private Set<Long> enseignantIds = new HashSet<>();

    private LocalDateTime createdAt;
}
