package com.projetTransversalIsi.security.infrastructure;

import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.security.domain.RoleRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JpaRoleRepository implements RoleRepository {

    private final SpringDataRoleRepository sprgDtRoleRepo;
    private  final EntityManager entityManager;
    private final RolerMapper roleMapper;

    @Override
    public Role save(Role role){

        Set<JpaPermissionEntity> jpaPermissions = role.getPermissions().stream()
                .map(perm -> entityManager.getReference(JpaPermissionEntity.class, perm.getName()))
                .collect(Collectors.toSet());

        JpaRoleEntity jpaRole= roleMapper.RoleToJpaRoleEntity(role,jpaPermissions);
        return roleMapper.JpaRoleEntityToRole(
                sprgDtRoleRepo.save(
                       jpaRole
                )
        );
    }

    @Override
    public Long count(){
        return sprgDtRoleRepo.count();
    }
    @Override
    public Optional<Role> getRoleById(String id){
        return sprgDtRoleRepo.findById(id).map(roleMapper::JpaRoleEntityToRole);
    }
}
