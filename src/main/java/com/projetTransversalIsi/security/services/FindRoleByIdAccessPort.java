package com.projetTransversalIsi.security.services;

import com.projetTransversalIsi.security.domain.Role;

public interface FindRoleByIdAccessPort {
    Role findRoleById(String id);
}
