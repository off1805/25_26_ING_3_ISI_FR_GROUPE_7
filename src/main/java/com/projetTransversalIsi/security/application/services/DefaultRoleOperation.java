package com.projetTransversalIsi.security.application.services;

import com.projetTransversalIsi.security.domain.Role;

public interface DefaultRoleOperation {
    Role registerNewRole(Role role);
    Long count();
}
