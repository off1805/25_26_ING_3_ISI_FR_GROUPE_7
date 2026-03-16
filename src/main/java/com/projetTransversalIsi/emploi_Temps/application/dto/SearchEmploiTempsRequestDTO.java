package com.projetTransversalIsi.emploi_Temps.application.dto;



import java.time.LocalDate;

public record SearchEmploiTempsRequestDTO(
        Long classeId,
        LocalDate date,
        Integer semaine,
        Boolean includeDeleted
) {}
