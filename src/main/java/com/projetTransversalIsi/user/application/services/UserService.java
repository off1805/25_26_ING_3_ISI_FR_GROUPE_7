package com.projetTransversalIsi.user.application.services;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserService  implements DefaultUserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserById(Long id){
       return userRepository.findById(id);
    }

}
