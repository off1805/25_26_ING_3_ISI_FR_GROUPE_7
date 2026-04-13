package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendance_code")
public class JpaAttendanceCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seance_id", nullable = false)
    private Long seanceId;

    @Column(name = "enseignant_id", nullable = false)
    private Long enseignantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AttendanceCode.CodeType type;

    @Column(name = "valeur", nullable = false, unique = true)
    private String valeur;

    @Column(name = "heures_a_marquer", nullable = false)
    private float heuresAMarquer;

    @Column(name = "duree_vie_minutes", nullable = false)
    private int dureeVieMinutes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
