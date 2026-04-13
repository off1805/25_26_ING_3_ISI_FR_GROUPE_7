package com.projetTransversalIsi.user.profil.services;

import com.projetTransversalIsi.user.profil.domain.Profile;

public interface InitProfile<T extends Profile>{
    T execute(T profile);
}
