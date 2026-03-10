package com.projetTransversalIsi.Filiere.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

public class Filiere {

    private Long id;
    private String code;
    private String nom;
    private String description;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public Filiere(String code, String nom, String description) {
        this.code = code.toUpperCase();
        this.nom = nom;
        this.description = description;
        this.deleted = false;
    }



    public void update(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public void delete() {
        if (this.deleted) {
            throw new IllegalStateException("La filière " + this.id + " est déjà supprimée");
        }
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        if (!this.deleted) {
            throw new IllegalStateException("La filière " + this.id + " n'est pas supprimée");
        }
        this.deleted = false;
        this.deletedAt = null;
    }

}
