package com.projetTransversalIsi.authentification.infrastructure;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target="userId", expression="java(mapJpaUserToId(jpaRefresh.getUser()))")
    RefreshToken JpaRefreshTokenToRefreshToken(JpaRefreshTokenEntity jpaRefresh);

    default JpaRefreshTokenEntity RefreshTokenToJpaRefreshToken( RefreshToken rToken,JpaUserEntity user){
        JpaRefreshTokenEntity jpaRToken= new JpaRefreshTokenEntity();
        jpaRToken.setToken(rToken.getToken());
        jpaRToken.setCreatedAt(rToken.getCreatedAt());
        jpaRToken.setRevoked(rToken.isRevoked());
        jpaRToken.setExpiresAt(rToken.getExpiresAt());
        jpaRToken.setUser(user);
        return jpaRToken;
    }

    default  Long mapJpaUserToId(JpaUserEntity jpaUser){
        return jpaUser.getId();
    }


}
