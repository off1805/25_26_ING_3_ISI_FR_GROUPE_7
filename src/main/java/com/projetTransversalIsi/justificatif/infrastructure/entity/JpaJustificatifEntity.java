package com.projetTransversalIsi.justificatif.infrastructure.entity;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "justificatif")
public class JpaJustificatifEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "justificatif_seance",
            joinColumns = @JoinColumn(name = "justificatif_id"))
    @Column(name = "seance_id")
    private List<Long> seanceIds = new ArrayList<>();

    @Column(name = "motif", nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "justificatif_fichier",
            joinColumns = @JoinColumn(name = "justificatif_id"))
    private List<FichierEmbeddable> fichiers = new ArrayList<>();

    @Column(name = "date_absence")
    private LocalDate dateAbsence;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Justificatif.Statut statut = Justificatif.Statut.PENDING;

    @Column(name = "commentaire_ap")
    private String commentaireAP;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
