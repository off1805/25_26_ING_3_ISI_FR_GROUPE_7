package com.projetTransversalIsi.security.infrastructure;


import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",uses = {PermissionMapper.class})
public interface RolerMapper {
    Role JpaRoleEntityToRole(JpaRoleEntity jpaRole);
    JpaRoleEntity RoleToJpaRoleEntity(Role role);

}
