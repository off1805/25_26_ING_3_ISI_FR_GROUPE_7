package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceListResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;

import java.util.List;

public interface GetPresenceListUC {
    PresenceListResponseDTO getById(Long id);
    List<PresenceRowResponseDTO> getRows(Long presenceListId);
}
