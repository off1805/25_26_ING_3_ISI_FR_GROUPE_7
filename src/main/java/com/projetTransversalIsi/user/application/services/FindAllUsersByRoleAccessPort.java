package com.projetTransversalIsi.user.application.services;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.security.domain.EnumRole;
import java.util.Set;

public interface FindAllUsersByRoleAccessPort {
    Set<User> findByIds(Set<String> ids, EnumRole role);
}
