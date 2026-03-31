package com.projetTransversalIsi.pedagogie.application.dto;

import jakarta.validation.constraints.NotNull;


public record CreateAnneeScolaireRequestDTO(
    @NotNull int anneeDebut,
    @NotNull int anneeFin){

}
