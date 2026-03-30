package com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "niveau")
public class JpaNiveauEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int ordre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id", nullable = false)
    private JpaFiliereEntity filiere;

    @OneToMany(mappedBy = "niveau", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaSpecialiteEntity> specialites = new ArrayList<>();
}
