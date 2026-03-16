package com.projetTransversalIsi.emploi_Temps.application.dto;


import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateEmploiTempsRequestDTO(

        @NotNull(message = "La date de début est obligatoire")
        LocalDate dateDebut,

        @NotNull(message = "La date de fin est obligatoire")
        LocalDate dateFin,

        Integer semaine,

        @NotNull(message = "La classe est obligatoire")
        Long classeId
) {}
