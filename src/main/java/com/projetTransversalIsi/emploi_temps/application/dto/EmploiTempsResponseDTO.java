package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.EmploiTemps;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record EmploiTempsResponseDTO(
        Long id,
        LocalDate dateDebut,
        LocalDate dateFin,
        Integer semaine,
        Long classeId,
        List<SeanceResponseDTO> seances,
        boolean deleted,
        LocalDateTime deletedAt
) {
    public static EmploiTempsResponseDTO fromDomain(EmploiTemps emploiTemps) {
        return new EmploiTempsResponseDTO(
                emploiTemps.getId(),
                emploiTemps.getDateDebut(),
                emploiTemps.getDateFin(),
                emploiTemps.getSemaine(),
                emploiTemps.getClasseId(),
                emploiTemps.getSeances().stream()
                        .map(SeanceResponseDTO::fromDomain)
                        .collect(Collectors.toList()),
                emploiTemps.isDeleted(),
                emploiTemps.getDeletedAt()
        );
    }
}
