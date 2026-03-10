package com.projetTransversalIsi.profil.services;

import com.projetTransversalIsi.profil.domain.Profile;

public interface InitProfileForNewUserAccessPort {
    Profile execute(Long id);
}
