package com.projetTransversalIsi.authentification.infrastructure;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target="userId", source="user.id")
    RefreshToken JpaRefreshTokenToRefreshToken(JpaRefreshTokenEntity jpaRefresh);

    @Mapping(target = "user",expression = "java(jpaUser)")
     JpaRefreshTokenEntity RefreshTokenToJpaRefreshToken( RefreshToken rToken,@Context JpaUserEntity jpaUser);

}
