package com.projetTransversalIsi.security.services;

import com.projetTransversalIsi.security.domain.Permission;

public interface DefaultPermissionOperation {
    Permission registerNewPermission(Permission perm);
    Long count();
}
