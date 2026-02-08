package com.projetTransversalIsi.profil.application.services;

import com.projetTransversalIsi.profil.domain.Profile;

public interface InitProfileForNewUserAccessPort {
    Profile execute(Long id);
}
