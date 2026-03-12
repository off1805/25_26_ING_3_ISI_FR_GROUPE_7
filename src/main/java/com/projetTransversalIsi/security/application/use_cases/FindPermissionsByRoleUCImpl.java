package com.projetTransversalIsi.security.application.use_cases;

import com.projetTransversalIsi.security.services.FindRoleByIdAccessPort;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FindPermissionsByRoleUCImpl implements FindPermissionsByRoleUC {

    private final FindRoleByIdAccessPort findRoleByIdAccessPort;

    @Override
    public Set<Permission> execute(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return Collections.emptySet();
        }
        try {
            Role role = findRoleByIdAccessPort.findRoleById(roleName);
            return role.getPermissions();
        } catch (RuntimeException e) {
            // Role not found, return empty set or throw custom exception depending on
            // requirements.
            // For now, return empty set as it's safe for UI.
            return Collections.emptySet();
        }
    }
}
