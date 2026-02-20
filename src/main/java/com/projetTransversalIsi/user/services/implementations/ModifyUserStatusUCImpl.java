package com.projetTransversalIsi.user.services.implementations;

import com.projetTransversalIsi.user.dto.ModifyUserStatusDTO;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import com.projetTransversalIsi.user.services.interfaces.ModifyUserStatusUC;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModifyUserStatusUCImpl implements ModifyUserStatusUC {

    private final UserRepository userRepo;

    @Override
    @Transactional
    public User execute(Long id, ModifyUserStatusDTO command) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setStatus(command.status());
        userRepo.save(user);
        return user;
    }
}
