package com.projetTransversalIsi.user.domain;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    User save(User user, String password, Set<Permission> permission, Profile profil);
    boolean userAlreadyExists(String email);
    Optional<User> findById(Long id);
}
