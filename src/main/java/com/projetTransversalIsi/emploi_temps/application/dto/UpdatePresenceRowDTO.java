package com.projetTransversalIsi.emploi_temps.application.dto;

public record UpdatePresenceRowDTO(
        Long id,
        boolean present,
        float heuresAbsence
) {}
