package com.projetTransversalIsi.user.application.dto;


import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserStatus;

public record UserDetailsResponseDTO(Long id, UserStatus status, String email, String roleName) {
    public static UserDetailsResponseDTO fromDomain(User user) {
        return new UserDetailsResponseDTO(
                user.getId(),
                user.getStatus(),
                user.getEmail(),
                user.getRole().getName()
        );
    }
}
