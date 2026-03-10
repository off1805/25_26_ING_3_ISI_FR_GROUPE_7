package com.projetTransversalIsi.user.services.implementations;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.services.interfaces.SearchUserUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchUserUCImpl implements SearchUserUC {
    private final UserRepository userRepo;

    @Override
    public List<User> execute(String userStatus, String email, String role, boolean deleted){
        return userRepo.search(userStatus,email,role,deleted);
    }
}
