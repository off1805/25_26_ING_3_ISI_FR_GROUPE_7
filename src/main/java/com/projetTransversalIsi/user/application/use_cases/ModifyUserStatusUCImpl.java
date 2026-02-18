package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.application.dto.ModifyUserStatusDTO;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
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
