package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;

public record CreateAttendanceCodeDTO(
        Long seanceId,
        Long enseignantId,
        AttendanceCode.CodeType type,
        float heuresAMarquer,
        int dureeVieMinutes
) {}
