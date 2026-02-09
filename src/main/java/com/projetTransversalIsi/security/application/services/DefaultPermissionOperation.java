package com.projetTransversalIsi.security.application.services;

import com.projetTransversalIsi.security.domain.Permission;

public interface DefaultPermissionOperation {
    Permission registerNewPermission(Permission perm);
    Long count();
}
