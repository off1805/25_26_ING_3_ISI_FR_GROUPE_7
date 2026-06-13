package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.RetardListResponseDTO;

import java.time.LocalDate;

public interface GetOrCreateRetardListUC {
    RetardListResponseDTO execute(Long classeId, LocalDate semaineDebut);
}
