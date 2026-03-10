package com.projetTransversalIsi.profil.services;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.security.domain.Role;

public interface ProfileSelectionStrategy {
    Profile selectProfileFor(Role role);
}
