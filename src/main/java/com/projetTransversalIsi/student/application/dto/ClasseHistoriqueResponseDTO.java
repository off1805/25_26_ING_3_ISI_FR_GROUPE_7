package com.projetTransversalIsi.student.application.dto;

import java.time.LocalDate;

public record ClasseHistoriqueResponseDTO(
        Long classeId,
        String classeCode,
        String classeDescription,
        Long specialiteId,
        String specialiteLibelle,
        LocalDate dateDebut,
        LocalDate dateFin,
        boolean estActif
) {}
