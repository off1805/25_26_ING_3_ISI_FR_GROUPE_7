package com.projetTransversalIsi.specialite.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

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

    /**
     * Code de la branche (filière) à laquelle cette spécialité appartient.
     * Ex : "RESEAU", "GENIE_LOGICIEL"
     */
    @Column(name = "branche_code", nullable = false)
    private String brancheCode;

    /**
     * Niveau d'études minimum pour accéder à cette spécialité.
     * Ex : 4 pour 4e année ingénieur.
     */
    @Column(name = "niveau_minimum", nullable = false)
    private int niveauMinimum;

    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    public void applyDefaults() {
        if (!active) {
            active = true;
        }
        if (code != null) {
            code = code.toUpperCase();
        }
        if (brancheCode != null) {
            brancheCode = brancheCode.toUpperCase();
        }
    }
}
