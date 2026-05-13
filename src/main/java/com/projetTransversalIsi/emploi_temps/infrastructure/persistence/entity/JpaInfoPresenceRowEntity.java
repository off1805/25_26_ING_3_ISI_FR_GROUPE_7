package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "info_presence_row")
public class JpaInfoPresenceRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;

    @Column(name = "presence_row_id")
    private Long presenceRowId;

    @Column(name = "appel_id", nullable = false)
    private Long appelId;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    // null = non déterminé, true = présent, false = absent
    @Column(name = "is_present")
    private Boolean isPresent;

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;
}
