package com.projetTransversalIsi.security.domain;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> getRoleById(String id);
    Long count();
    Long countPermissionLinks();
    Role save(Role role);
}
