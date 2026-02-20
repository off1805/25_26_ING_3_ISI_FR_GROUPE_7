package com.projetTransversalIsi.classe.infrastructure;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class JpaClasseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int effectif;

    @Column(nullable = false)
    private String nom;
}
