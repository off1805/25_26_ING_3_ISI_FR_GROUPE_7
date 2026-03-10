package com.projetTransversalIsi.user.dto;

import com.projetTransversalIsi.user.domain.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ModifyUserStatusDTO(
        @NotNull UserStatus status) {
}
