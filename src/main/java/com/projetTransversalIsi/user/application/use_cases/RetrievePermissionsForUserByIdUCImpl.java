package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.security.application.services.FindAllPermissionByIdsAccessPort;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.user.application.services.DefaultUserService;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class RetrievePermissionsForUserByIdUCImpl implements RetrievePermissionsForUserByIdUC {

    private final FindAllPermissionByIdsAccessPort findAllPermissionByIdsAccessPort;
    private final DefaultUserService defaultUserService;

    @Override
    @Transactional
    public Set<Permission> execute(Long id){
        User user= defaultUserService.findUserById(id).orElseThrow(()-> new UserNotFoundException(id));
        Set<String> permissionsId= user.getIdPermissions();
        return findAllPermissionByIdsAccessPort.findAllPermissionByIds(permissionsId);
    }
}
