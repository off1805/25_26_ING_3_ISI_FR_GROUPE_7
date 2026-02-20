package com.projetTransversalIsi.security.services;

import com.projetTransversalIsi.security.domain.Role;

public interface DefaultRoleOperation {
    Role registerNewRole(Role role);
    Long count();
}
