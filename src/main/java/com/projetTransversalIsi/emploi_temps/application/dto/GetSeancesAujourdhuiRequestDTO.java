package com.projetTransversalIsi.emploi_temps.application.dto;

/**
 * DTO de requête pour récupérer les séances d'un enseignant le jour même.
 *
 * @param enseignantId  Identifiant de l'enseignant (obligatoire)
 * @param includeDeleted Inclure les séances supprimées (soft-delete) — false par défaut
 */
public record GetSeancesAujourdhuiRequestDTO(
        Long enseignantId,
        Boolean includeDeleted
) {}
