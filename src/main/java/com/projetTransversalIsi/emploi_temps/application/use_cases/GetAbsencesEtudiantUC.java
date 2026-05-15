package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AbsenceEtudiantDTO;

import java.util.List;

public interface GetAbsencesEtudiantUC {
    List<AbsenceEtudiantDTO> execute(Long etudiantId);
}
