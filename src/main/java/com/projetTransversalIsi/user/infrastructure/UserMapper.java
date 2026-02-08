package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.security.infrastructure.JpaPermissionEntity;
import com.projetTransversalIsi.security.infrastructure.JpaRoleEntity;
import com.projetTransversalIsi.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

   default JpaUserEntity UserToJpaUserEntity(User user,String password, JpaRoleEntity jpaRole, Set<JpaPermissionEntity> perm,JpaProfileEntity jpaProfil){
        JpaUserEntity jpaUserEn= new JpaUserEntity();
        jpaUserEn.setId(user.getId());
        jpaUserEn.setEmail(user.getEmail());
        jpaUserEn.setPassword(password);
        jpaUserEn.setStatus(user.getStatus());
        jpaUserEn.setRole(jpaRole);
        jpaUserEn.setPermissions(perm);
        jpaUserEn.setProfile(jpaProfil);
        return jpaUserEn;
    }

    @Mapping(target="idPermissions", expression="java(mapJpaPermissionToIds(jpaUser.getPermissions()))")
    User JpaUseEntityToUser(JpaUserEntity jpaUser);

    default Set<String> mapJpaPermissionToIds(Set<JpaPermissionEntity> perm) {
        if (perm == null) return null;
        return perm.stream()
                .map(JpaPermissionEntity::getName)
                .collect(Collectors.toSet());
    }

}
