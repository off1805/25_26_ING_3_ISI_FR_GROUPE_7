package com.projetTransversalIsi.emploi_temps.application.dto;

import java.time.LocalDate;

/**
 * DTO de requête pour récupérer les séances d'un enseignant
 * sur une semaine entière (lundi → dimanche de la semaine contenant {@code dateDeReference}).
 *
 * @param enseignantId    Identifiant de l'enseignant (obligatoire)
 * @param dateDeReference N'importe quelle date appartenant à la semaine souhaitée.
 *                        Si null, la semaine courante est utilisée.
 * @param includeDeleted  Inclure les séances supprimées (soft-delete) — false par défaut
 */
public record GetSeancesSemaineRequestDTO(
        Long enseignantId,
        LocalDate dateDeReference,
        Boolean includeDeleted
) {}
