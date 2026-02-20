package com.projetTransversalIsi.user.services.implementations;


import com.projetTransversalIsi.profil.services.InitProfile;
import com.projetTransversalIsi.profil.services.ProfileSelectionStrategy;
import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.security.services.FindAllPermissionByIdsAccessPort;
import com.projetTransversalIsi.security.services.FindRoleByIdAccessPort;
import com.projetTransversalIsi.security.services.PasswordHasherAC;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.user.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserAlreadyExistsException;
import com.projetTransversalIsi.user.services.interfaces.CreateUserUC;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CreateUserUCImpl implements CreateUserUC {

    private final UserRepository userRepo;
    private final InitProfile initProfile;
    private final ProfileSelectionStrategy profileSelectionStrategy;
    private final FindRoleByIdAccessPort getRoleById;
    private final FindAllPermissionByIdsAccessPort getAllPermById;
    private final PasswordHasherAC passwordHasher;

    @Transactional
    @Override
    public User execute( CreateUserRequestDTO command){
        if(userRepo.userAlreadyExists(command.email())){
           throw new UserAlreadyExistsException(command.email());
        }

        String hashPassword= passwordHasher.encode(command.password());
        Role role= getRoleById.findRoleById(command.idRole());
        Profile profile = profileSelectionStrategy.selectProfileFor(role);
        Profile newProfile= initProfile.execute(profile);
        Set<Permission> permissions= getAllPermById.findAllPermissionByIds(command.idPermissions());
        User user = new User(command.email(), role);
        user.setPassword(hashPassword);
        user.setProfileId(newProfile.getId());
        user.setPermissions(permissions);
        return userRepo.registerNewUser(user);
    }
}
