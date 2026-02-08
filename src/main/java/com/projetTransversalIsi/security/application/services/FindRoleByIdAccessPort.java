package com.projetTransversalIsi.security.application.services;

import com.projetTransversalIsi.security.domain.Role;

public interface FindRoleByIdAccessPort {
    Role findRoleById(String id);
}
