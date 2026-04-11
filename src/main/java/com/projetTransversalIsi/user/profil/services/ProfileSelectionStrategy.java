package com.projetTransversalIsi.user.profil.services;

import com.projetTransversalIsi.user.profil.application.ProfilCreationDTO;
import com.projetTransversalIsi.user.profil.domain.Profile;
import com.projetTransversalIsi.security.domain.Role;

public interface ProfileSelectionStrategy {
    Profile selectProfileFor(Role role, ProfilCreationDTO profileJson);
}
