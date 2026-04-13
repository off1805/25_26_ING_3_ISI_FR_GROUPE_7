package com.projetTransversalIsi.user.profil.domain;
import com.projetTransversalIsi.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Profile {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String numeroTelephone;
    private String photoUrl;

}
