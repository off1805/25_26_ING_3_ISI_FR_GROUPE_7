package com.projetTransversalIsi.security.domain;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> getRoleById(String id);
    Long count();
    Role save(Role role);
}
