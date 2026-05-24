package com.projetTransversalIsi.web_application.ap.repository;

public interface AbsenceEtudiantRow {
    Long getEtudiantId();
    String getNom();
    String getPrenom();
    String getMatricule();
    String getPhotoUrl();
    Long getClasseId();
    String getClasseCode();
    Long getTotalEnregistrements();
    Long getTotalAbsences();
    Long getNbJustifiees();
    Long getNbEnAttente();
}
