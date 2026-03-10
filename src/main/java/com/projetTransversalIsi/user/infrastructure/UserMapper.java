package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.security.infrastructure.PermissionMapper;
import com.projetTransversalIsi.security.infrastructure.RolerMapper;
import com.projetTransversalIsi.user.domain.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {RolerMapper.class, PermissionMapper.class})
public interface UserMapper {

    @Mapping(target = "profile",expression = "java(jpaProfile)")
    JpaUserEntity UserToJpaUserEntity(User user, @Context JpaProfileEntity jpaProfile);

    @Mapping(source = "profile.id", target = "profileId")
    User JpaUseEntityToUser(JpaUserEntity jpaUser);

}
