package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.UpdatePresenceRowDTO;

public interface UpdatePresenceRowUC {
    PresenceRowResponseDTO execute(UpdatePresenceRowDTO dto);
}
