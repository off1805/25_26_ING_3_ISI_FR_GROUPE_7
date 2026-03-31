package com.projetTransversalIsi.emploi_temps.application.usecase;

import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesAujourdhuiRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;

import java.util.List;

/**
 * Use case : récupérer les séances d'un enseignant pour le <strong>jour même</strong>.
 *
 * <p>Le jour courant est déterminé au moment de l'exécution via {@link java.time.LocalDate#now()}.</p>
 */
public interface GetSeancesEnseignantAujourdhuiUseCase {

    /**
     * Retourne la liste des séances de l'enseignant identifié par
     * {@code request.enseignantId()} dont la date est égale à aujourd'hui.
     *
     * @param request critères de recherche (enseignantId, includeDeleted)
     * @return liste (éventuellement vide) des {@link SeanceResponseDTO}
     */
    List<SeanceResponseDTO> execute(GetSeancesAujourdhuiRequestDTO request);
}
