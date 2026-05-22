package com.projetTransversalIsi.emploi_temps.application.dto;

/**
 * Projection native SQL pour les statistiques de présence par matière
 * d'un étudiant. Mappée directement depuis la requête agrégée.
 */
public interface MatiereStatsRow {
    Long   getOffreUeId();
    String getLibelle();
    String getCode();
    Integer getCredit();
    Integer getVolumeHoraireTotal();
    Integer getSemestre();
    String getCouleur();
    String getDescription();
    Long   getTotalMinutesProgrammes();
    Long   getMinutesAbsence();
    Long   getNbAbsences();
    Long   getNbNonJustifiees();
    Long   getNbEnCours();
    Long   getNbJustifiees();
    Long   getNbRejetees();
}
