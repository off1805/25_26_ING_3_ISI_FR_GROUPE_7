package com.projetTransversalIsi.web_application.ap.dto;

import java.util.List;

public record SeancePresenceDetailDTO(
        SeanceApDTO seance,
        Long presenceListId,
        List<String> creneauxHoraires,
        List<SeancePresenceEtudiantDTO> etudiants
) {}
