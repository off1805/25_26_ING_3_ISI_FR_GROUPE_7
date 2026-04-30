package com.projetTransversalIsi.user.services;


import com.projetTransversalIsi.common.application.services.EmailService;
import com.projetTransversalIsi.user.profil.services.InitProfile;
import com.projetTransversalIsi.user.profil.services.ProfileSelectionStrategy;
import com.projetTransversalIsi.user.profil.domain.Profile;
import com.projetTransversalIsi.security.services.FindAllPermissionByIdsAccessPort;
import com.projetTransversalIsi.security.services.FindRoleByIdAccessPort;
import com.projetTransversalIsi.security.services.PasswordHasherAC;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.user.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserAlreadyExistsException;
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
    private final EmailService emailService;

    @Transactional
    @Override
    public User execute( CreateUserRequestDTO command){
        if(userRepo.userAlreadyExists(command.email())){
           throw new UserAlreadyExistsException(command.email());
        }

        String hashPassword= passwordHasher.encode("password");
        Role role= getRoleById.findRoleById(command.idRole());
        System.out.println(command);
        System.out.println("Before Create user execution");
        System.out.println(command.profile());

        Profile profile = profileSelectionStrategy.selectProfileFor(role,command.profile());
        System.out.println("Create user execution");
        System.out.println(profile.getId());
        System.out.println(profile.getNom());
        System.out.println(profile.getPrenom());
        System.out.println(profile.getNumeroTelephone());
        Profile newProfile= initProfile.execute(profile);
        Set<Permission> permissions= getAllPermById.findAllPermissionByIds(command.idPermissions());
        User user = new User(command.email(), role);
        user.setPassword(hashPassword);
        user.setProfile(newProfile);
        user.setPermissions(permissions);
        User savedUser = userRepo.registerNewUser(user);

        // Send emails
        String fullName = newProfile.getPrenom() + " " + newProfile.getNom();
        emailService.sendInitPassword(savedUser.getEmail(), "password", fullName);
        emailService.sendConfirmationEmail(savedUser.getEmail(), fullName);

        return savedUser;
    }
}
