package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AttendanceCodeResponseDTO;

import java.util.List;

public interface GetAttendanceCodeUC {
    AttendanceCodeResponseDTO getById(Long id);
    List<AttendanceCodeResponseDTO> getBySeanceId(Long seanceId);
}
