package com.projetTransversalIsi.security.application.services;

import com.projetTransversalIsi.security.domain.Permission;

import java.util.Optional;
import java.util.Set;

public interface FindAllPermissionByIdsAccessPort {
    Set<Permission> findAllPermissionByIds(Set<String> ids);
}
