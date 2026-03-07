package com.projetTransversalIsi.specialite.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Specialite {

    private Long id;
    private String code;
    private String libelle;
    private String description;

    /**
     * La branche (filière) à laquelle appartient cette spécialité.
     * Ex : "RESEAU", "GENIE_LOGICIEL", "SYSTEMES_EMBARQUES"
     */
    private String brancheCode;

    /**
     * Le niveau minimum requis pour accéder à cette spécialité.
     * Ex : 4 (4e année). Les niveaux inférieurs ont une spécialité par défaut "TRONC_COMMUN".
     */
    private int niveauMinimum;

    private boolean active = true;

    public Specialite(String code, String libelle, String description, String brancheCode, int niveauMinimum) {
        this.code = code;
        this.libelle = libelle;
        this.description = description;
        this.brancheCode = brancheCode;
        this.niveauMinimum = niveauMinimum;
        this.active = true;
    }

    public void desactiver() {
        this.active = false;
    }

    public void activer() {
        this.active = true;
    }
}
