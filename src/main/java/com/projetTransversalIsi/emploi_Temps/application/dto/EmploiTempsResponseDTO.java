package com.projetTransversalIsi.emploi_Temps.application.dto;



import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.seance.application.dto.SeanceResponseDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record EmploiTempsResponseDTO(
        Long id,
        String libelle,
        LocalDate dateDebut,
        LocalDate dateFin,
        Integer semaine,
        Long filiereId,
        Long niveauId,
        List<SeanceResponseDTO> seances,
        boolean deleted,
        LocalDateTime deletedAt
) {
    public static EmploiTempsResponseDTO fromDomain(EmploiTemps emploiTemps) {
        return new EmploiTempsResponseDTO(
                emploiTemps.getId(),
                emploiTemps.getLibelle(),
                emploiTemps.getDateDebut(),
                emploiTemps.getDateFin(),
                emploiTemps.getSemaine(),
                emploiTemps.getFiliereId(),
                emploiTemps.getNiveauId(),
                emploiTemps.getSeances().stream()
                        .map(SeanceResponseDTO::fromDomain)
                        .collect(Collectors.toList()),
                emploiTemps.isDeleted(),
                emploiTemps.getDeletedAt()
        );
    }
}