package com.projetTransversalIsi.security.application.use_cases;

import com.projetTransversalIsi.security.domain.Permission;
import java.util.Set;

public interface FindPermissionsByRoleUC {
    Set<Permission> execute(String roleName);
}
