package com.projetTransversalIsi.emploi_temps.application.usecase;

import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesEnseignantRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;

import java.util.List;

/**
 * Use case <strong>générique</strong> : récupérer les séances d'un enseignant
 * soit pour un <em>jour précis</em>, soit pour <em>toute une semaine ISO</em>.
 *
 * <p>Le comportement est déterminé par {@link GetSeancesEnseignantRequestDTO#periode()} :</p>
 * <ul>
 *   <li>{@code JOUR}    → équivalent au use case {@link GetSeancesEnseignantAujourdhuiUseCase}</li>
 *   <li>{@code SEMAINE} → équivalent au use case {@link GetSeancesEnseignantParSemaineUseCase}</li>
 * </ul>
 */
public interface GetSeancesEnseignantUseCase {

    /**
     * Retourne les séances de l'enseignant pour la période spécifiée.
     *
     * @param request critères de recherche (enseignantId, periode, dateDeReference, includeDeleted)
     * @return liste (éventuellement vide) des {@link SeanceResponseDTO},
     *         triée par date puis heure de début
     * @throws IllegalArgumentException si {@code request.enseignantId()} est null
     *                                  ou si {@code request.periode()} est null
     */
    List<SeanceResponseDTO> execute(GetSeancesEnseignantRequestDTO request);
}
