package com.projetTransversalIsi.profil.domain;

public interface ProfileRepository<T extends Profile> {
    T save(T profile);
}
