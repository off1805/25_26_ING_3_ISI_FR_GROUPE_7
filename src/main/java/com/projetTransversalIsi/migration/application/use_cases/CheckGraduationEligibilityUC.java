package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.GraduationEligibilityDTO;

public interface CheckGraduationEligibilityUC {
    GraduationEligibilityDTO execute(Long classeId);
}
