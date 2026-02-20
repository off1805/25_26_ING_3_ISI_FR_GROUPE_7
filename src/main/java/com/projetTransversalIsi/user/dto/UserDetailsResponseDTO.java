package com.projetTransversalIsi.user.dto;


import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.enums.UserStatus;

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
