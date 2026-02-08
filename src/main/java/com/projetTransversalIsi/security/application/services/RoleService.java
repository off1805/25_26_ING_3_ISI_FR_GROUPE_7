package com.projetTransversalIsi.security.application.services;

import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.security.domain.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleService implements
        FindRoleByIdAccessPort,
        DefaultRoleOperation {


    private final RoleRepository roleRepo;

    @Override
    public Role findRoleById(String id){
        return roleRepo.getRoleById(id)
                .orElseThrow(()->new RuntimeException("Role non trouve avec cet id"));
    }

    @Override
    public Role registerNewRole(Role role){
        return  roleRepo.save(role);
    }

    @Override
    public Long count(){
        return roleRepo.count();
    }
}
