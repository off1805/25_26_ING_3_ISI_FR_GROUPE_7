package com.projetTransversalIsi.pedagogie.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lien (enseignant, classe) dans une offre_ue.
 * Remplace le simple Set<Long> enseignantIds pour porter la classe
 * dans laquelle l'enseignant dispense le cours.
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EnseignantClasseLink {

    @Column(name = "enseignant_id", nullable = false)
    private Long enseignantId;

    @Column(name = "classe_id", nullable = false)
    private Long classeId;
}
