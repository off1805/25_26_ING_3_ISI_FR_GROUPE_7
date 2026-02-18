package com.projetTransversalIsi.user.application.dto;

import com.projetTransversalIsi.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ModifyUserStatusDTO(
        @NotNull UserStatus status) {
}
