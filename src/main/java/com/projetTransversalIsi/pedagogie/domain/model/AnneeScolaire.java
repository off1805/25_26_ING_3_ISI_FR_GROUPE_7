package com.projetTransversalIsi.pedagogie.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnneeScolaire {
    Long id;
    int anneeDebut;
    int anneeFin;
}
