package com.projetTransversalIsi.security.application.services;

import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionService implements
        FindAllPermissionByIdsAccessPort,
        DefaultPermissionOperation{

    private final PermissionRepository perRepo;
    @Override
    public Set<Permission> findAllPermissionByIds(Set<String> ids){
        if(ids.isEmpty()) return Collections.emptySet();
        return perRepo.getAllPermissionsByIds(ids).orElse(Collections.emptySet());
    }

    @Override
    public Permission registerNewPermission(Permission perm){
        return perRepo.save(perm);
    }

    @Override
    public Long count(){
        return perRepo.count();
    }
}
