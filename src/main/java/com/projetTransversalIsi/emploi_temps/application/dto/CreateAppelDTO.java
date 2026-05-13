package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.Appel;

import java.time.LocalTime;
import java.util.List;

// presenceListId : liste de présence ouverte pour la séance.
// seanceId : nécessaire pour valider que [heureDebut, heureFin] est dans la plage de la séance.
// dureeVieMinutes : ignoré pour MANUEL (mis à 0).
// etudiantIds : liste des étudiants de la classe — utilisée pour créer les InfoPresenceRow
//               avec isPresent=null dès le lancement de l'appel. Peut être null.
public record CreateAppelDTO(
        Long presenceListId,
        Long seanceId,
        Long enseignantId,
        Appel.TypeAppel typeAppel,
        LocalTime heureDebut,
        LocalTime heureFin,
        int dureeVieMinutes,
        List<Long> etudiantIds
) {}
