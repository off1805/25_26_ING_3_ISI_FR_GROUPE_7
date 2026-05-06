package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.DecisionJustificatifDTO;
import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;

public interface RejeterJustificatifUC {
    JustificatifResponseDTO execute(DecisionJustificatifDTO dto);
}
