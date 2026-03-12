package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.security.domain.Permission;

import java.util.Set;

public interface RetrievePermissionsForUserByIdUC {
    Set<Permission> execute(Long id);
}
