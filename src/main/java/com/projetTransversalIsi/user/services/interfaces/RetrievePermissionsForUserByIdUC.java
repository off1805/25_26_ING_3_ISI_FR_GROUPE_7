package com.projetTransversalIsi.user.services.interfaces;

import com.projetTransversalIsi.security.domain.Permission;

import java.util.Set;

public interface RetrievePermissionsForUserByIdUC {
    Set<Permission> execute(Long id);
}
