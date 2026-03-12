package com.projetTransversalIsi.seance.application.dto;

import java.time.LocalDate;

public record SearchSeanceRequestDTO(
        LocalDate date,
        Long enseignantId,
        Long coursId,
        String salle,
        Boolean includeDeleted
) {}