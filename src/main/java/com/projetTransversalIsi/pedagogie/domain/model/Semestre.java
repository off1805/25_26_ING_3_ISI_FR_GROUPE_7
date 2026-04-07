package com.projetTransversalIsi.pedagogie.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Semestre {
    private Long id;
    private Integer numero; // 1 ou 2
    private String libelle; // "Semestre 1" ou "Semestre 2"
    private Long anneeScolaireId;
    private Long specialiteId;
}