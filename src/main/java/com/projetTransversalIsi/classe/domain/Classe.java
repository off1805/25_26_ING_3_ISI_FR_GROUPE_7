package com.projetTransversalIsi.classe.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.projetTransversalIsi.specialite.domain.Specialite;

@Getter
@Setter
@NoArgsConstructor
public class Classe {
    private Long id;
    private String code;
    private String description;
    private Specialite specialite;

    public Classe(String code, String description, Specialite specialite) {
        this.code = code;
        this.description = description;
        this.specialite = specialite;
    }

    public void update(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
