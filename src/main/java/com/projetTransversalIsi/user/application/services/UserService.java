package com.projetTransversalIsi.user.application.services;

import com.projetTransversalIsi.user.application.dto.DeleteUserRequestDTO;
import com.projetTransversalIsi.user.application.use_cases.DeleteUserUC;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Executor;



@Component
@RequiredArgsConstructor
public class UserService  implements DefaultUserService {
    private final UserRepository userRepository;
    private final DeleteUserUC deleteUserUC;

    @Override
    public Optional<User> findUserById(Long id){
       return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {

        DeleteUserRequestDTO command = new DeleteUserRequestDTO(userId);



        deleteUserUC.execute(command);
    }

}
