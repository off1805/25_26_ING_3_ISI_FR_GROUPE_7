package com.projetTransversalIsi.pedagogie.application.dto;

import lombok.Data;

@Data
public class OffreUeFiltreDto {
    private Long specialiteId;
    private Integer semestre;
    private String libelle;
}
