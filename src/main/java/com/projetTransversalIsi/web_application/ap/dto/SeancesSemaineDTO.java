package com.projetTransversalIsi.web_application.ap.dto;

import java.time.LocalDate;
import java.util.List;

public record SeancesSemaineDTO(
        LocalDate dateDebut,
        LocalDate dateFin,
        List<SeanceApDTO> seances
) {}
