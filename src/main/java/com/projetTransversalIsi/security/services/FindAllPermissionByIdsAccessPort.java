package com.projetTransversalIsi.security.services;

import com.projetTransversalIsi.security.domain.Permission;

import java.util.Set;

public interface FindAllPermissionByIdsAccessPort {
    Set<Permission> findAllPermissionByIds(Set<String> ids);
}
