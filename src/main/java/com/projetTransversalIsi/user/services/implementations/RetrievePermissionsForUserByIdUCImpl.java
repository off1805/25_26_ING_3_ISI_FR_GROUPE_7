package com.projetTransversalIsi.user.services.implementations;

import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.user.services.interfaces.DefaultUserService;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import com.projetTransversalIsi.user.services.interfaces.RetrievePermissionsForUserByIdUC;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@RequiredArgsConstructor
public class RetrievePermissionsForUserByIdUCImpl implements RetrievePermissionsForUserByIdUC {

    private final DefaultUserService defaultUserService;

    @Override
    @Transactional
    public Set<Permission> execute(Long id){
        User user= defaultUserService.findUserById(id).orElseThrow(()-> new UserNotFoundException(id));
        return user.getPermissions();
    }
}
