package com.projetTransversalIsi.emploi_temps.application.dto;

import java.time.LocalDate;

/**
 * DTO de requête générique permettant de récupérer les séances d'un enseignant
 * soit pour un <em>jour précis</em>, soit pour <em>toute une semaine</em>.
 *
 * <p>Règles de résolution de la période :</p>
 * <ul>
 *   <li>Si {@code periode == JOUR} : {@code dateDeReference} est utilisée comme jour cible
 *       (aujourd'hui si null).</li>
 *   <li>Si {@code periode == SEMAINE} : la semaine ISO (lundi–dimanche) contenant
 *       {@code dateDeReference} est utilisée (semaine courante si null).</li>
 * </ul>
 *
 * @param enseignantId    Identifiant de l'enseignant (obligatoire)
 * @param periode         {@link PeriodeType#JOUR} ou {@link PeriodeType#SEMAINE}
 * @param dateDeReference Date de référence pour le calcul de la période (null → aujourd'hui)
 * @param includeDeleted  Inclure les séances supprimées (soft-delete) — false par défaut
 */
public record GetSeancesEnseignantRequestDTO(
        Long enseignantId,
        PeriodeType periode,
        LocalDate dateDeReference,
        Boolean includeDeleted
) {

    /** Type de période de recherche. */
    public enum PeriodeType {
        JOUR,
        SEMAINE
    }
}
