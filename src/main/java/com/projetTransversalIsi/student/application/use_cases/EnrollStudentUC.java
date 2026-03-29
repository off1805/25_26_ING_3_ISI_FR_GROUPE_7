package com.projetTransversalIsi.student.application.use_cases;

import com.projetTransversalIsi.student.application.dto.EnrollStudentRequestDTO;
import com.projetTransversalIsi.student.application.dto.EnrollStudentResponseDTO;

public interface EnrollStudentUC {
    EnrollStudentResponseDTO execute(EnrollStudentRequestDTO command);
}
