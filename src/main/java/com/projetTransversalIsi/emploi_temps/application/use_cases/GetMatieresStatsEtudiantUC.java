package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.MatiereStatsEtudiantDTO;

import java.util.List;

public interface GetMatieresStatsEtudiantUC {
    /**
     * @param etudiantId  profileId de l'étudiant (student_profile.id)
     * @param classeId    id de la classe de l'étudiant
     * @param specialiteId id de la spécialité (filtre les offre_ue de l'année active)
     */
    List<MatiereStatsEtudiantDTO> execute(Long etudiantId, Long classeId, Long specialiteId);
}
