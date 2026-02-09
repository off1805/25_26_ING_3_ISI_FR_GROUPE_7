package com.projetTransversalIsi.security.infrastructure;


import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RolerMapper {
    @Mapping(target="idPermissions", expression="java(mapJpaPermissionToIds(jpaRole.getPermissions()))")
    Role JpaRoleEntityToRole(JpaRoleEntity jpaRole);

    default JpaRoleEntity RoleToJpaRoleEntity(Role role, Set<JpaPermissionEntity> perm){
        JpaRoleEntity jpaRoleEn= new JpaRoleEntity();
        jpaRoleEn.setName(role.getName());
        jpaRoleEn.setPermissions(perm);
        return jpaRoleEn;

    }

    default Set<String> mapJpaPermissionToIds(Set<JpaPermissionEntity> perm) {
        if (perm == null) return null;
        return perm.stream()
                .map(JpaPermissionEntity::getName)
                .collect(Collectors.toSet());
    }
}
