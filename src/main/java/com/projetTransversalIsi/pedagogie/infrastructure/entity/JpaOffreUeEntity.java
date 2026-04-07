package com.projetTransversalIsi.pedagogie.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(
    name = "offre_ue",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_offre_ue_annee",
        columnNames = {"ue_id", "annee_scolaire_id"}
    )
)
public class JpaOffreUeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ue_id", nullable = false)
    private JpaUeEntity ue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private JpaAnneeScolaireEntity anneeScolaire;

    // Champs recopiés depuis l'UE au moment de la création de l'offre
    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int credit;

    @Column(nullable = false)
    private int volumeHoraireTotal;

    private String description;

    @Column(nullable = false, columnDefinition = "varchar(255) default '#ffffff'")
    private String couleur;

    @Column(name = "specialite_id")
    private Long specialiteId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "offre_ue_enseignant",
        joinColumns = @JoinColumn(name = "offre_ue_id")
    )
    @Column(name = "enseignant_id")
    private Set<Long> enseignantIds = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void applyDefaults() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
