package com.projetTransversalIsi.Niveau.domain;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.specialite.domain.Specialite;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Niveau {

    private Long id;
    private int ordre;
    private String description;
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    private Filiere filiere;
    private List<Specialite> specialites = new ArrayList<>();

    public Niveau(int ordre, String description, Filiere filiere) {
        this.ordre = ordre;
        this.description = description;
        this.filiere = filiere;
    }

    public void update(int ordre, String description, Filiere filiere) {
        this.ordre = ordre;
        this.description = description;
        this.filiere = filiere;
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }
}
