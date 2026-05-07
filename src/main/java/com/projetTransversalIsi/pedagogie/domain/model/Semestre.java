package com.projetTransversalIsi.pedagogie.domain.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class Semestre {
    private Long id;
    private Integer numero; // 1 ou 2
    private String libelle; // "Semestre 1" ou "Semestre 2"
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long anneeScolaireId;
    private Long niveauId;
}