package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.security.domain.Permission;

import java.util.Optional;
import java.util.Set;

public interface RetrievePermissionsForUserByIdUC {
    Set<Permission> execute(Long id);
}
