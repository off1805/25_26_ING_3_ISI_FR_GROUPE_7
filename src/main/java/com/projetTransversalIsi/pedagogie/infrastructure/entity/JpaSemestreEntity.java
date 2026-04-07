package com.projetTransversalIsi.pedagogie.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "semestre",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_semestre_annee_specialite_numero",
                        columnNames = {"annee_scolaire_id", "specialite_id", "numero"}
                )
        }
)
@Getter
@Setter
public class JpaSemestreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 50)
    private String libelle;

    @Column(name = "annee_scolaire_id", nullable = false)
    private Long anneeScolaireId;

    @Column(name = "specialite_id", nullable = false)
    private Long specialiteId;
}