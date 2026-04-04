package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.CreatePresenceRowDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;

public interface AddPresenceRowUC {
    PresenceRowResponseDTO execute(CreatePresenceRowDTO dto);
}
