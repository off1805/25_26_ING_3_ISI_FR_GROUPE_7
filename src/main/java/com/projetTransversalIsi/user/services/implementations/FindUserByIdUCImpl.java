package com.projetTransversalIsi.user.services.implementations;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import com.projetTransversalIsi.user.services.interfaces.FindUserByIdUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindUserByIdUCImpl implements FindUserByIdUC {

    private final UserRepository userRepo;

    @Override
    public User execute(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
