package com.projetTransversalIsi.specialite.infrastructure;

import com.projetTransversalIsi.Niveau.infrastructure.JpaNiveauEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "specialite")
public class JpaSpecialiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id", nullable = false)
    private JpaNiveauEntity niveau;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "specialite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.projetTransversalIsi.classe.infrastructure.JpaClasseEntity> classes = new ArrayList<>();

    @PrePersist
    public void applyDefaults() {
        if (!active) {
            active = true;
        }
        if (code != null) {
            code = code.toUpperCase();
        }
    }
}
