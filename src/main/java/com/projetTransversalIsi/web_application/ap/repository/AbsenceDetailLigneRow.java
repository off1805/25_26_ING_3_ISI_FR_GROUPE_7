package com.projetTransversalIsi.web_application.ap.repository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AbsenceDetailLigneRow {
    Long getSeanceId();
    LocalDate getDate();
    LocalTime getHeureDebut();
    LocalTime getHeureFin();
    String getSalle();
    String getUeLibelle();
    String getUeCode();
    Boolean getPresent();
    String getJustificatifStatut();
}
