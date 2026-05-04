package com.projetTransversalIsi.security.domain;

import java.util.List;

public record UserPrincipal(
        Long userId,
        String email,
        String role,
        List<String> permissions
) {}
