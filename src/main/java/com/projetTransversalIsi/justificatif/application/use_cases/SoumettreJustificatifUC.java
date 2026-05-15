package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.application.dto.SoumettreJustificatifDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SoumettreJustificatifUC {
    JustificatifResponseDTO execute(SoumettreJustificatifDTO dto, List<MultipartFile> fichiers);
}
