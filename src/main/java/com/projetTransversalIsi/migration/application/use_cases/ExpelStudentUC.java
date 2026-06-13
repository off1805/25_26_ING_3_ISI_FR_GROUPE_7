package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.ExpulsionResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ExpelStudentUC {
    ExpulsionResponseDTO execute(Long userId, String motif, MultipartFile justificatif);
}
