package com.projetTransversalIsi.user.domain;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.List;

public interface UserRepository {
    User save(User user, String password, Set<Permission> permission, Profile profil);

    User save(User user);

    boolean userAlreadyExists(String email);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<String> findPasswordMatchEmail(String email);
    List<User> getAllUserOfStaff();
    List<User> findAllDeleted();
    List<User> findDeletedSince(LocalDateTime since);

    void save(User user, String password, Set<String> idPermissions, Object profil);
}
