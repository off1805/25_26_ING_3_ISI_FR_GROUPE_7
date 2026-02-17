package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.infrastructure.JpaPermissionEntity;
import com.projetTransversalIsi.security.infrastructure.JpaRoleEntity;
import com.projetTransversalIsi.security.infrastructure.PermissionMapper;
import com.projetTransversalIsi.security.infrastructure.RolerMapper;
import com.projetTransversalIsi.user.domain.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",uses = {RolerMapper.class, PermissionMapper.class})
public interface UserMapper {

    @Mapping(target = "profile",expression = "java(jpaProfile)")
    JpaUserEntity UserToJpaUserEntity(User user,@Context JpaProfileEntity jpaProfile);

    @Mapping(source = "profile.id", target = "profileId")
    User JpaUseEntityToUser(JpaUserEntity jpaUser);

}
