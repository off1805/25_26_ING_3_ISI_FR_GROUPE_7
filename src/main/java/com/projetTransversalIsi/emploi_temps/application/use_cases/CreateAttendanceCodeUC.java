package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AttendanceCodeResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.CreateAttendanceCodeDTO;

public interface CreateAttendanceCodeUC {
    AttendanceCodeResponseDTO execute(CreateAttendanceCodeDTO dto);
}
