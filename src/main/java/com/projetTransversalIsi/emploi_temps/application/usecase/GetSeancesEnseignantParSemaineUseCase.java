package com.projetTransversalIsi.emploi_temps.application.usecase;

import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesSemaineRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;

import java.util.List;

/**
 * Use case : récupérer <strong>toutes les séances</strong> d'un enseignant
 * pour une <strong>semaine ISO donnée</strong> (lundi → dimanche).
 *
 * <p>La semaine est calculée à partir de {@code request.dateDeReference()} :
 * n'importe quel jour de la semaine souhaitée peut être fourni.
 * Si la date est null, la semaine en cours est utilisée.</p>
 */
public interface GetSeancesEnseignantParSemaineUseCase {

    /**
     * Retourne la liste des séances de l'enseignant identifié par
     * {@code request.enseignantId()} comprises entre le lundi et le dimanche
     * de la semaine contenant {@code request.dateDeReference()}.
     *
     * @param request critères de recherche (enseignantId, dateDeReference, includeDeleted)
     * @return liste (éventuellement vide) des {@link SeanceResponseDTO}, triée par date puis heure de début
     */
    List<SeanceResponseDTO> execute(GetSeancesSemaineRequestDTO request);
}
