package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.application.dto.SoumettreJustificatifDTO;
import org.springframework.web.multipart.MultipartFile;

public interface SoumettreJustificatifUC {
    JustificatifResponseDTO execute(SoumettreJustificatifDTO dto, MultipartFile fichier);
}
