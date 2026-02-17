package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.profil.infrastructure.JpaProfileEntity;
import com.projetTransversalIsi.security.domain.EnumRole;
import com.projetTransversalIsi.security.infrastructure.JpaPermissionEntity;
import com.projetTransversalIsi.security.infrastructure.JpaRoleEntity;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
    public User registerNewUser(User user){
        JpaRoleEntity jpaRole= entityManager.getReference(JpaRoleEntity.class,user.getRole().getName());
        JpaProfileEntity jpaProfile = user.getProfileId() == null ? null : entityManager.getReference(JpaProfileEntity.class, user.getProfileId());
        Set<JpaPermissionEntity> jpaPermissions = user.getPermissions() == null ? Set.of() : user.getPermissions().stream()
                        .map(perm -> entityManager.getReference(JpaPermissionEntity.class, perm.getName()))
                        .collect(Collectors.toSet());
        JpaUserEntity jpaUser= userMapper.UserToJpaUserEntity(user, jpaProfile);
        JpaUserEntity newEntity= jpaRepo.save(jpaUser);

        log.info(newEntity.toString());
        return  userMapper.JpaUseEntityToUser(newEntity);
    }

    @Override
    public void save(User user) {
        JpaProfileEntity jpaProfile= entityManager.getReference(JpaProfileEntity.class,user.getProfileId());
        JpaUserEntity savedEntity = jpaRepo.save(userMapper.UserToJpaUserEntity(user,jpaProfile));
        log.info("User updated: {}", savedEntity.getId());

    }



    @Override
    public boolean userAlreadyExists(String email){
        return jpaRepo.existsByEmail(email);
    }


    @Override
    public Optional<User> findById(Long id){
        return jpaRepo.findById(id).map(userMapper::JpaUseEntityToUser);
    }

    @Override
    public Optional<String> findPasswordMatchEmail(String email){
        return jpaRepo.findPasswordByEmail(email);
}
    @Override
    public Optional<User> findByEmail(String email){
        return jpaRepo.findByEmail(email).map(userMapper::JpaUseEntityToUser);
    }

    @Override
    public List<User> getAllUserOfStaff(){
        return jpaRepo.findByRoleIdNotIn(List.of(EnumRole.ADMIN.name(),EnumRole.STUDENT.name())).stream().map(userMapper::JpaUseEntityToUser).toList();
    }
    @Override
    public List<User> findAllDeleted() {
        return jpaRepo.findByDeletedTrue().stream()
                .map(userMapper::JpaUseEntityToUser)
                .collect(Collectors.toList());
    }


    @Override
    public List<User> findDeletedSince(LocalDateTime since) {
        return jpaRepo.findDeletedSince(since).stream()
                .map(userMapper::JpaUseEntityToUser)
                .collect(Collectors.toList());
    }
}
