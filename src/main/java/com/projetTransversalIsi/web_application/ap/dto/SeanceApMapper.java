package com.projetTransversalIsi.web_application.ap.dto;

import com.projetTransversalIsi.web_application.ap.repository.SeanceApRow;

import java.time.LocalDate;
import java.time.LocalTime;

public final class SeanceApMapper {

    private SeanceApMapper() {}

    public static SeanceApDTO toDto(SeanceApRow row) {
        return new SeanceApDTO(
                row.getSeanceId(),
                row.getLibelle(),
                row.getSalle(),
                row.getDateSeance(),
                row.getHeureDebut(),
                row.getHeureFin(),
                row.getClasseId(),
                row.getClasseCode(),
                row.getType(),
                row.getEnseignantNom(),
                row.getEnseignantPrenom(),
                computeStatut(row.getDateSeance(), row.getHeureDebut(), row.getHeureFin()),
                row.getPresenceListId(),
                row.getNbPresents(),
                row.getNbAbsents(),
                row.getNbTotal());
    }

    public static String computeStatut(LocalDate dateSeance, LocalTime heureDebut, LocalTime heureFin) {
        LocalDate today = LocalDate.now();
        if (dateSeance.isBefore(today)) return "TERMINE";
        if (dateSeance.isAfter(today)) return "A_VENIR";

        LocalTime now = LocalTime.now();
        if (heureFin.isBefore(now))      return "TERMINE";
        if (heureDebut.isAfter(now))     return "A_VENIR";
        return "EN_COURS";
    }
}
