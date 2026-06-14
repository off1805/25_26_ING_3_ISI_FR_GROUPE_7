package com.projetTransversalIsi.web_application.ap.repository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface SeanceApRow {
    Long getSeanceId();
    String getLibelle();
    String getSalle();
    LocalDate getDateSeance();
    LocalTime getHeureDebut();
    LocalTime getHeureFin();
    Long getClasseId();
    String getClasseCode();
    String getType();
    String getEnseignantNom();
    String getEnseignantPrenom();
    Long getPresenceListId();
    Long getNbPresents();
    Long getNbAbsents();
    Long getNbTotal();
}
