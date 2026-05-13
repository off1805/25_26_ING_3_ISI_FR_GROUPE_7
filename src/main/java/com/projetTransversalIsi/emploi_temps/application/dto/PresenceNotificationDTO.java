package com.projetTransversalIsi.emploi_temps.application.dto;

public record PresenceNotificationDTO(
        Long etudiantId,
        String nom,
        String prenom,
        String photoUrl,
        Long presenceListId
) {}
