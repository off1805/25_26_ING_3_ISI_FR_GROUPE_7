package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;

public interface MarkStudentPresentUC {
    PresenceRowResponseDTO execute(MarkStudentPresentCommand command);
}
