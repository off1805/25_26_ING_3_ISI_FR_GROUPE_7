package com.projetTransversalIsi.student.application.use_cases;

import com.projetTransversalIsi.student.application.dto.ClasseHistoriqueResponseDTO;

import java.util.List;

public interface GetStudentClasseHistoryUC {
    List<ClasseHistoriqueResponseDTO> execute(Long userId);
}
