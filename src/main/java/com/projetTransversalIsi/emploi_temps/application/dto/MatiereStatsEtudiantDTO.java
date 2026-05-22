package com.projetTransversalIsi.emploi_temps.application.dto;

/**
 * Statistiques de présence d'un étudiant pour une unité d'enseignement.
 *
 * totalMinutesProgrammes : somme des durées des séances passées de la classe pour cette UE.
 * minutesAbsence         : minutes d'absence réelles de l'étudiant (depuis InfoPresenceRow
 *                          si disponible, sinon durée totale de la séance).
 * tauxPresence           : (totalMinutesProgrammes - minutesAbsence) / totalMinutesProgrammes × 100.
 */
public record MatiereStatsEtudiantDTO(
        Long    offreUeId,
        String  libelle,
        String  code,
        int     credit,
        int     volumeHoraireTotal,
        Integer semestre,
        String  couleur,
        String  description,
        long    totalMinutesProgrammes,
        long    minutesAbsence,
        long    nbAbsences,
        int     tauxPresence,
        long    nbNonJustifiees,
        long    nbEnCours,
        long    nbJustifiees,
        long    nbRejetees
) {
    public static MatiereStatsEtudiantDTO from(MatiereStatsRow row) {
        long total  = row.getTotalMinutesProgrammes() != null ? row.getTotalMinutesProgrammes() : 0L;
        long absent = row.getMinutesAbsence()         != null ? row.getMinutesAbsence()         : 0L;
        int  taux   = total > 0 ? (int) Math.round(((double)(total - absent) / total) * 100) : 100;

        return new MatiereStatsEtudiantDTO(
                row.getOffreUeId(),
                row.getLibelle(),
                row.getCode(),
                row.getCredit()            != null ? row.getCredit()            : 0,
                row.getVolumeHoraireTotal() != null ? row.getVolumeHoraireTotal() : 0,
                row.getSemestre(),
                row.getCouleur(),
                row.getDescription(),
                total,
                absent,
                row.getNbAbsences()        != null ? row.getNbAbsences()        : 0L,
                taux,
                row.getNbNonJustifiees()   != null ? row.getNbNonJustifiees()   : 0L,
                row.getNbEnCours()         != null ? row.getNbEnCours()         : 0L,
                row.getNbJustifiees()      != null ? row.getNbJustifiees()      : 0L,
                row.getNbRejetees()        != null ? row.getNbRejetees()        : 0L
        );
    }
}
