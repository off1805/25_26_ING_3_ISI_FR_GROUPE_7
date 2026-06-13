package com.projetTransversalIsi.migration.infrastructure.entity;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "student_expulsion")
public class JpaStudentExpulsionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private JpaStudentProfileEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private JpaClasseEntity classe;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(name = "justificatif_url", nullable = false)
    private String justificatifUrl;

    @Column(name = "justificatif_nom_original")
    private String justificatifNomOriginal;

    @Column(name = "expelled_at", nullable = false)
    private LocalDateTime expelledAt;

    @PrePersist
    public void prePersist() {
        if (expelledAt == null) {
            expelledAt = LocalDateTime.now();
        }
    }
}
