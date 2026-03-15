package com.projetTransversalIsi.Filiere.infrastructure;

import com.projetTransversalIsi.Niveau.infrastructure.JpaNiveauEntity;
import com.projetTransversalIsi.cycle.infrastructure.JpaCycleEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "filiere")
public class JpaFiliereEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 500)
    private String description;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "cycle_id", nullable = false)
    private JpaCycleEntity cycle;

    @OneToMany(mappedBy = "filiere")
    private List<JpaNiveauEntity> niveaux = new ArrayList<>();
}
