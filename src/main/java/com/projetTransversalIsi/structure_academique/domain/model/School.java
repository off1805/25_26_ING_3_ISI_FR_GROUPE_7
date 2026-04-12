package com.projetTransversalIsi.structure_academique.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class School {
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private double rayon;
}
