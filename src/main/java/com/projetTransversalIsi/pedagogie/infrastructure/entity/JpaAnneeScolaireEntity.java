package com.projetTransversalIsi.pedagogie.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "annee_scolaire")
public class JpaAnneeScolaireEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true,nullable = false)
    int anneeDebut;
    @Column(unique = true,nullable = false)
    int anneeFin;
}
