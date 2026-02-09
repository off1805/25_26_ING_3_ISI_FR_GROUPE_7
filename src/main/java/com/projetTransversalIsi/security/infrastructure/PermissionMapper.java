package com.projetTransversalIsi.security.infrastructure;

import com.projetTransversalIsi.security.domain.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission JpaPermissionEntityToPermission(JpaPermissionEntity jpaPermEn);
    JpaPermissionEntity PermissionToJpaPermissionEntity(Permission permission);
}
