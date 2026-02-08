package com.projetTransversalIsi.security.infrastructure;

import com.projetTransversalIsi.security.application.services.FindAllPermissionByIdsAccessPort;
import com.projetTransversalIsi.security.domain.Permission;
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
    private final FindAllPermissionByIdsAccessPort getAllPermById;

    @Override
    public Role save(Role role){

        Set<Permission> permission= getAllPermById.findAllPermissionByIds(role.getIdPermissions());
        Set<JpaPermissionEntity> jpaPermissions = permission.stream()
                .map(perm -> entityManager.getReference(JpaPermissionEntity.class, perm.getName()))
                .collect(Collectors.toSet());
        return roleMapper.JpaRoleEntityToRole(
                sprgDtRoleRepo.save(
                        roleMapper.RoleToJpaRoleEntity(role,jpaPermissions)
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
