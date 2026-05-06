package com.projetTransversalIsi.justificatif.infrastructure.entity;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "justificatif")
public class JpaJustificatifEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;

    @Column(name = "seance_id")
    private Long seanceId;

    @Column(name = "motif", nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(name = "fichier_url")
    private String fichierUrl;

    @Column(name = "date_absence", nullable = false)
    private LocalDate dateAbsence;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Justificatif.Statut statut = Justificatif.Statut.PENDING;

    @Column(name = "commentaire_ap")
    private String commentaireAP;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
