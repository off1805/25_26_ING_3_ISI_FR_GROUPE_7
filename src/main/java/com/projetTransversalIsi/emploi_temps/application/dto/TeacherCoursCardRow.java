package com.projetTransversalIsi.emploi_temps.application.dto;

public interface TeacherCoursCardRow {
    Long    getOffreUeId();
    String  getLibelle();
    String  getCode();
    String  getCouleur();
    Integer getSemestre();
    Integer getCredit();
    Integer getVolumeHoraireTotal();
    Long    getClasseId();
    String  getClasseCode();
    Long    getNbEtudiants();
    Long    getNbSeancesFaites();
}
