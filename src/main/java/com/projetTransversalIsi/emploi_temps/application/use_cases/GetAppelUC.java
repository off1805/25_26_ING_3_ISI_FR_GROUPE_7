package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AppelResponseDTO;

import java.util.List;

public interface GetAppelUC {
    AppelResponseDTO getById(Long id);
    List<AppelResponseDTO> getByPresenceListId(Long presenceListId);
}
