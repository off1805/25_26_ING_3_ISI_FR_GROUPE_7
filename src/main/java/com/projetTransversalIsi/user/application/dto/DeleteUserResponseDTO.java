package com.projetTransversalIsi.user.application.dto;

import com.projetTransversalIsi.user.domain.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record DeleteUserResponseDTO(
        long userId,
        String deletedAt,
        String email
) {
    public static DeleteUserResponseDTO fromUser(User user) {
        return new DeleteUserResponseDTO(
                user.getId(),
                user.getDeletedAt() != null
                        ? user.getDeletedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                user.getEmail()
        );
    }
}