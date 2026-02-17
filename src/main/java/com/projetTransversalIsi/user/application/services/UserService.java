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
public class UserService  implements
        DefaultUserService ,
        GetPasswordByEmail,
        GetUserByEmail{
    private final UserRepository userRepository;
    private final DeleteUserUC deleteUserUC;

    @Override
    public Optional<User> findUserById(Long id){
       return userRepository.findById(id);
    }

    @Override
    public Optional<String> getPasswordByEmail(String email){
        return userRepository.findPasswordMatchEmail(email);
    }

    @Override
    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }
    @Override
    @Transactional
    public void deleteUser(long userId) {

        DeleteUserRequestDTO command = new DeleteUserRequestDTO(userId);



        deleteUserUC.execute(command);
    }

}
