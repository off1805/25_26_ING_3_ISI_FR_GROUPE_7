package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.application.dto.UserDetailsResponseDTO;
import com.projetTransversalIsi.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUserStaffUCImpl implements GetAllUserStaffUC{

    private final UserRepository userRepo;
    @Override
    public List<UserDetailsResponseDTO> execute(){
        return userRepo.getAllUserOfStaff().stream().map(UserDetailsResponseDTO::fromDomain).toList();
    }
}
