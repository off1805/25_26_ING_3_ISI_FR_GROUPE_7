package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.application.dto.DeleteUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class DeleteUserUCImpl implements DeleteUserUC {

    private final UserRepository userRepo;

    @Override
    @Transactional
    public void execute(DeleteUserRequestDTO command) {
        User user = userRepo.findById(command.id())
                .orElseThrow(() -> new UserNotFoundException(command.id()));
        if (user.isDeleted()) {
            throw new IllegalStateException("L'utilisateur " + command.id() + " est déjà supprimé");
        }
        user.delete();
        userRepo.save(user);
    }
}
