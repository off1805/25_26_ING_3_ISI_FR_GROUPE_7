package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "seance")
public class JpaSeanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "salle")
    private String salle;

    @Column(name = "date_seance", nullable = false)
    private LocalDate dateSeance;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    // Nullable : null pour les événements
    @Column(name = "cours_id")
    private Long coursId;

    // Nullable : null pour les événements
    @Column(name = "enseignant_id")
    private Long enseignantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Seance.TypeSeance type = Seance.TypeSeance.SEANCE;

    @Column(name = "couleur", length = 32)
    private String couleur;

    @Column(name = "icon_key", length = 64)
    private String iconKey;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
