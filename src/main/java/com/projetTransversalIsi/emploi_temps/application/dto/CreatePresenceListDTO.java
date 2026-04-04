package com.projetTransversalIsi.emploi_temps.application.dto;

import java.time.LocalDate;

public record CreatePresenceListDTO(
        Long seanceId,
        Long classeId,
        Long ueId,
        Long enseignantId,
        LocalDate date
) {}
