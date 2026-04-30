package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.user.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.UserProfileMapper;
import com.projetTransversalIsi.security.infrastructure.PermissionMapper;
import com.projetTransversalIsi.security.infrastructure.RolerMapper;
import com.projetTransversalIsi.user.domain.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {RolerMapper.class, PermissionMapper.class, UserProfileMapper.class})
public interface UserMapper {

    @Mapping(target = "profile",expression = "java(jpaProfile)")
    JpaUserEntity UserToJpaUserEntity(User user, @Context JpaProfileEntity jpaProfile);

    User JpaUseEntityToUser(JpaUserEntity jpaUser);
}
