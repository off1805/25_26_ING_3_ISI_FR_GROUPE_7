package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.security.infrastructure.JpaPermissionEntity;
import com.projetTransversalIsi.security.infrastructure.JpaRoleEntity;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository jpaRepo;
    private final UserMapper userMapper;
    private  final EntityManager entityManager;

    @Override
    public User save(User user, String password, Set<Permission> permission, Profile profil){
        JpaRoleEntity jpaRole= entityManager.getReference(JpaRoleEntity.class,user.getRole().getName());
        JpaProfileEntity jpaProfile= entityManager.getReference(JpaProfileEntity.class,profil.getId());
        Set<JpaPermissionEntity> jpaPermissions = permission.stream()
                .map(perm -> entityManager.getReference(JpaPermissionEntity.class, perm.getName()))
                .collect(Collectors.toSet());
        JpaUserEntity jpaUser= userMapper.UserToJpaUserEntity(user,password,jpaRole,jpaPermissions,jpaProfile);
        JpaUserEntity newEntity= jpaRepo.save(jpaUser);

        log.info(newEntity.toString());
        return  userMapper.JpaUseEntityToUser(newEntity);
    }


    @Override
    public boolean userAlreadyExists(String email){
        return jpaRepo.existsByEmail(email);
    }


    @Override
    public Optional<User> findById(Long id){
        return jpaRepo.findById(id).map(userMapper::JpaUseEntityToUser);
    }


}
