package com.projetTransversalIsi.profil.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilCreationDTO {
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom;
    @NotBlank
    private String matricule;
    @NotBlank
    private String numeroTelephone;
}
