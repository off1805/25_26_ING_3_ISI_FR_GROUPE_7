package com.projetTransversalIsi.emploi_temps.application.dto;

public record CreatePresenceRowDTO(
        Long presenceListId,
        Long etudiantId,
        boolean present
) {}
