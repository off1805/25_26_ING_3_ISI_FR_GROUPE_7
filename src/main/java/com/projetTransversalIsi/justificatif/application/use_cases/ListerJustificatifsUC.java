package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;

import java.util.List;

public interface ListerJustificatifsUC {
    List<JustificatifResponseDTO> getByEtudiant(Long etudiantId);
    List<JustificatifResponseDTO> getAll();
}
