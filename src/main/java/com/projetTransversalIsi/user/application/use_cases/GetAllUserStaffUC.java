package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.application.dto.UserDetailsResponseDTO;

import java.util.List;

public interface GetAllUserStaffUC {
    List<UserDetailsResponseDTO> execute();
}
