package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.CreatePresenceListDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceListResponseDTO;

public interface CreatePresenceListUC {
    PresenceListResponseDTO execute(CreatePresenceListDTO dto);
}
