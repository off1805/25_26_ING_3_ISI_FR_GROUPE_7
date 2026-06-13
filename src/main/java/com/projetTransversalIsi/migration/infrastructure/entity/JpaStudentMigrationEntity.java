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
@Table(name = "student_migration")
public class JpaStudentMigrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private JpaStudentProfileEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_source_id", nullable = false)
    private JpaClasseEntity classeSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_destination_id", nullable = false)
    private JpaClasseEntity classeDestination;

    @Column(nullable = false)
    private boolean executed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
