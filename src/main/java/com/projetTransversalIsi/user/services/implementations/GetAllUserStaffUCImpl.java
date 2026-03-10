package com.projetTransversalIsi.user.services.implementations;

import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.services.interfaces.GetAllUserStaffUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUserStaffUCImpl implements GetAllUserStaffUC {

    private final UserRepository userRepo;
    @Override
    public List<UserDetailsResponseDTO> execute(){
        return userRepo.getAllUserOfStaff().stream().map(UserDetailsResponseDTO::fromDomain).toList();
    }
}
