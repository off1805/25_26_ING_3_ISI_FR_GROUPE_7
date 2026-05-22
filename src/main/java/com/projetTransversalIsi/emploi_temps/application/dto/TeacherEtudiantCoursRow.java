package com.projetTransversalIsi.emploi_temps.application.dto;

public interface TeacherEtudiantCoursRow {
    Long   getEtudiantId();
    String getNom();
    String getPrenom();
    String getMatricule();
    String getPhotoUrl();
    Long   getNbAbsences();
    Long   getMinutesAbsence();
    Long   getTotalMinutesProgrammes();
}
