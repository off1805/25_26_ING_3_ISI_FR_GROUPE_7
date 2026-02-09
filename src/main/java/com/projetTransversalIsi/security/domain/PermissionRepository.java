package com.projetTransversalIsi.security.domain;

import java.util.Optional;
import java.util.Set;

public interface PermissionRepository {
    Optional<Set<Permission>> getAllPermissionsByIds(Set<String> ids);
    Long count();
    Permission save(Permission permission);
}
