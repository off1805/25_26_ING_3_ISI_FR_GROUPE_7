package com.projetTransversalIsi.user.profil.domain;


import com.projetTransversalIsi.user.profil.application.ProfilCreationDTO;

/*
 Cette interface gere la creation des profiles en fonction des roles
 */
public interface ProfileProvider {
    boolean supports(String roleName);
    Profile create(ProfilCreationDTO profileJson);
}
