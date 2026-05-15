package com.projetTransversalIsi.justificatif.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FichierEmbeddable {

    @Column(name = "fichier_url", nullable = false, columnDefinition = "TEXT")
    private String fichierUrl;

    @Column(name = "nom_original", columnDefinition = "TEXT")
    private String nomOriginal;
}
