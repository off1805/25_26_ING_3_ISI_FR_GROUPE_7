package com.projetTransversalIsi.emploi_Temps.infrastructure;

import com.projetTransversalIsi.seance.infrastructure.JpaSeanceEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "emploi_temps")
public class JpaEmploiTempsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    private Integer semaine;

    @Column(name = "classe_id", nullable = false)
    private Long classeId;

    // Unidirectional: emploi connaît ses séances; FK dans seance table
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "emploi_temps_id")
    private Set<JpaSeanceEntity> seances = new HashSet<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
