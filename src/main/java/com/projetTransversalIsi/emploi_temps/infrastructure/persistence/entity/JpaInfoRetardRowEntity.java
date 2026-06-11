package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "info_retard_row")
public class JpaInfoRetardRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;

    @Column(name = "retard_row_id", nullable = false)
    private Long retardRowId;

    @Column(name = "jour_semaine", nullable = false)
    private int jourSemaine;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "en_retard", nullable = false)
    private boolean enRetard = false;

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;
}
