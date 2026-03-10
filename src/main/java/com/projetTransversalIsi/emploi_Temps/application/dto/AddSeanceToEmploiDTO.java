package com.projetTransversalIsi.emploi_Temps.application.dto;



import jakarta.validation.constraints.NotNull;

public record AddSeanceToEmploiDTO(
        @NotNull(message = "L'ID de l'emploi du temps est obligatoire")
        Long emploiTempsId,

        @NotNull(message = "L'ID de la séance est obligatoire")
        Long seanceId
) {}