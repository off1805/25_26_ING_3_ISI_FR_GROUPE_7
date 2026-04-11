package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.EmploiStatus;

import java.time.LocalDate;
import java.util.List;

public record SearchEmploiTempsRequestDTO(
        Long classeId,
        LocalDate date,
        Integer semaine,
        Boolean includeDeleted,
        String status,
        LocalDate startDateAfter,
        LocalDate endDateBefore,
        LocalDate startDateBefore,
        LocalDate endDateAfter
) {}
