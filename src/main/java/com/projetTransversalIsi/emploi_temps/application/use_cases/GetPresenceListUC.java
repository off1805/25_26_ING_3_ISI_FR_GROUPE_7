package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.InfoPresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceListResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;

import java.util.List;

public interface GetPresenceListUC {
    PresenceListResponseDTO getById(Long id);
    List<PresenceRowResponseDTO> getRows(Long presenceListId);
    // Retourne toutes les InfoPresenceRow de la liste (pour affichage par-appel côté frontend).
    List<InfoPresenceRowResponseDTO> getInfoRows(Long presenceListId);
}
