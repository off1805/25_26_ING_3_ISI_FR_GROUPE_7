package com.projetTransversalIsi.profil.application.services;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.security.domain.Role;

public interface ProfileSelectionStrategy {
    Profile selectProfileFor(Role role);
}
