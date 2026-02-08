package com.projetTransversalIsi.profil.domain;
import com.projetTransversalIsi.user.domain.User;
import lombok.*;

@Getter
@Setter
public abstract class Profile {
    private Long id;
    private String matricule;

}
