package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.security.domain.RoleRepository;
import com.projetTransversalIsi.user.dto.UpdateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUserUCImpl implements UpdateUserUC {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    @Override
    @Transactional
    public User execute(Long id, UpdateUserRequestDTO command) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setEmail(command.email());
        user.setStatus(command.status());

        Role role = roleRepo.getRoleById(command.role())
                .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable : " + command.role()));
        user.setRole(role);

        userRepo.save(user);
        return user;
    }
}
