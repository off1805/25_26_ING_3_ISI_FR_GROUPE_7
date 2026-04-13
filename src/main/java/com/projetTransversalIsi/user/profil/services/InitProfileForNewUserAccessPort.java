package com.projetTransversalIsi.user.profil.services;

import com.projetTransversalIsi.user.profil.domain.Profile;

public interface InitProfileForNewUserAccessPort {
    Profile execute(Long id);
}
