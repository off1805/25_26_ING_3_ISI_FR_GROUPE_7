package com.projetTransversalIsi.security.infrastructure;

import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JpaPermissionRepository implements PermissionRepository {

    private final SpringDataPermissionRepository sprgDataPermRepo;
    private final PermissionMapper permissionMapper;

    @Override
    public Permission save(Permission permission){
        return permissionMapper
                .JpaPermissionEntityToPermission(
                        sprgDataPermRepo.save(permissionMapper.PermissionToJpaPermissionEntity(permission))
                );


    }

    @Override
    public Long count(){
        return sprgDataPermRepo.count();
    }

    @Override
    public Optional<Set<Permission>> getAllPermissionsByIds(Set<String> ids){
        return Optional.ofNullable(ids)
                        .filter(list -> !list.isEmpty())
                        .map(list -> sprgDataPermRepo.findAllById(ids)
                                .stream()
                                .map(permissionMapper::JpaPermissionEntityToPermission)
                                .collect(Collectors.toSet())
                        );

    }
}
