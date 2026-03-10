package com.projetTransversalIsi.profil.services;

import com.projetTransversalIsi.profil.domain.Profile;

public interface InitProfile<T extends Profile>{
    T execute(T profile);
}
