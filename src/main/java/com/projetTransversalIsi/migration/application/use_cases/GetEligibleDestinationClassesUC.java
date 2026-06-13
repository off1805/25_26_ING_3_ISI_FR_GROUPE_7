package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.EligibleClasseDTO;

import java.util.List;

public interface GetEligibleDestinationClassesUC {
    List<EligibleClasseDTO> execute(Long classeSourceId);
}
