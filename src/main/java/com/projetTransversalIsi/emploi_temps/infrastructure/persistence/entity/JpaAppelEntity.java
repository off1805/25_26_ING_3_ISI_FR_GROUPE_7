package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import com.projetTransversalIsi.emploi_temps.domain.model.Appel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "appel")
public class JpaAppelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "presence_list_id", nullable = false)
    private Long presenceListId;

    @Column(name = "enseignant_id", nullable = false)
    private Long enseignantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_appel", nullable = false)
    private Appel.TypeAppel typeAppel;

    // Null pour MANUEL.
    @Column(name = "valeur")
    private String valeur;

    // Null pour MANUEL.
    @Column(name = "attendance_code_id")
    private Long attendanceCodeId;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "duree_vie_minutes", nullable = false)
    private int dureeVieMinutes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
