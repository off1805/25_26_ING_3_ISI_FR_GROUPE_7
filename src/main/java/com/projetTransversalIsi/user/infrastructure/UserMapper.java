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

<<<<<<< Updated upstream
    @Mapping(target = "profile",expression = "java(jpaProfile)")
    JpaUserEntity UserToJpaUserEntity(User user, @Context JpaProfileEntity jpaProfile);

    @Mapping(source = "profile.id", target = "profileId")
=======
   default JpaUserEntity UserToJpaUserEntity(User user,String password, JpaRoleEntity jpaRole, Set<JpaPermissionEntity> perm,JpaProfileEntity jpaProfil){
        JpaUserEntity jpaUserEn= new JpaUserEntity();
        jpaUserEn.setId(user.getId());
        jpaUserEn.setEmail(user.getEmail());
        jpaUserEn.setPassword(password);
        jpaUserEn.setStatus(user.getStatus());
        jpaUserEn.setRole(jpaRole);
        jpaUserEn.setPermissions(perm);
        jpaUserEn.setProfile(jpaProfil);
        jpaUserEn.setDeleted(user.isDeleted());
        jpaUserEn.setDeletedAt(user.getDeletedAt());
        return jpaUserEn;
    }

    @Mapping(target="idPermissions", expression="java(mapJpaPermissionToIds(jpaUser.getPermissions()))")
    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
>>>>>>> Stashed changes
    User JpaUseEntityToUser(JpaUserEntity jpaUser);

}
