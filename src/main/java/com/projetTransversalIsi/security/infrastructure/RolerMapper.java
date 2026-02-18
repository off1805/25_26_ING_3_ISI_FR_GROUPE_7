package com.projetTransversalIsi.security.infrastructure;


import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",uses = {PermissionMapper.class})
public interface RolerMapper {

    Role JpaRoleEntityToRole(JpaRoleEntity jpaRole);

    @Mapping(target = "permissions",expression = "java(jpaPerm)")
    JpaRoleEntity RoleToJpaRoleEntity(Role role,@Context Set<JpaPermissionEntity> jpaPerm);




}
