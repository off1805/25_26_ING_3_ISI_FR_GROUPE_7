package com.projetTransversalIsi.pedagogie.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(
        name = "semestre",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_semestre_annee_niveau_numero",
                        columnNames = {"annee_scolaire_id", "niveau_id", "numero"}
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

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "annee_scolaire_id", nullable = false)
    private Long anneeScolaireId;

    @Column(name = "niveau_id", nullable = false)
    private Long niveauId;
}