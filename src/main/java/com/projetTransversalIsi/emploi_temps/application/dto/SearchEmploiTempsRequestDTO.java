package com.projetTransversalIsi.emploi_temps.application.dto;

import java.time.LocalDate;

public record SearchEmploiTempsRequestDTO(
        Long classeId,
        LocalDate date,
        Integer semaine,
        Boolean includeDeleted
) {}
