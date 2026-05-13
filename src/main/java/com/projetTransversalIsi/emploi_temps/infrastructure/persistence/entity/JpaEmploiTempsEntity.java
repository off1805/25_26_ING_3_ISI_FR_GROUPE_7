package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "emploi_temps")
public class   JpaEmploiTempsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    private Integer semaine;

    @Column(name = "classe_id", nullable = false)
    private Long classeId;

    // Relation unidirectionnelle : la FK emploi_temps_id est dans la table seance.
    // LAZY pour éviter de charger toutes les séances lors d'une liste d'emplois du temps.
    // Cascade MERGE uniquement : la création de séances passe par seanceRepo.save() avant addSeance().
    // Pas de CascadeType.REMOVE ici ; la suppression des séances est gérée explicitement dans le service.
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "emploi_temps_id")
    private Set<JpaSeanceEntity> seances = new HashSet<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
