package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;

import java.util.List;

public interface GetAllUserStaffUC {
    List<UserDetailsResponseDTO> execute();
}
