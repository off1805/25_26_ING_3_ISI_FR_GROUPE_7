package com.projetTransversalIsi.emploi_temps.application.dto;

import java.time.LocalDate;

public record SearchSeanceRequestDTO(
        LocalDate date,
        Long enseignantId,
        Long coursId,
        Boolean includeDeleted
) {}
