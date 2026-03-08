package com.projetTransversalIsi.emploi_Temps.application.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateEmploiTempsRequestDTO(

        @NotNull(message = "L'ID est obligatoire")
        Long id,

        @NotBlank(message = "Le libellé est obligatoire")
        String libelle,

        @NotNull(message = "La date de début est obligatoire")
        LocalDate dateDebut,

        @NotNull(message = "La date de fin est obligatoire")
        LocalDate dateFin,

        Integer semaine,

        @NotNull(message = "L'ID de la filière est obligatoire")
        Long filiereId,

        @NotNull(message = "L'ID du niveau est obligatoire")
        Long niveauId
) {}