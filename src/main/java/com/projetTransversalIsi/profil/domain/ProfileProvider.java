package com.projetTransversalIsi.profil.domain;


/*
 Cette interface gere la creation des profiles en fonction des roles
 */
public interface ProfileProvider {
    boolean supports(String roleName);
    Profile create();
}
