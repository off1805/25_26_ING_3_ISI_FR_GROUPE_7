package com.projetTransversalIsi.emploi_temps.application.dto;

public record TeacherEtudiantCoursDTO(
        Long   etudiantId,
        String nom,
        String prenom,
        String matricule,
        String photoUrl,
        long   nbAbsences,
        long   minutesAbsence,
        long   totalMinutesProgrammes,
        int    tauxPresence
) {
    public static TeacherEtudiantCoursDTO from(TeacherEtudiantCoursRow row) {
        long total  = row.getTotalMinutesProgrammes() != null ? row.getTotalMinutesProgrammes() : 0L;
        long absent = row.getMinutesAbsence()         != null ? row.getMinutesAbsence()         : 0L;
        int  taux   = total > 0 ? (int) Math.round(((double)(total - absent) / total) * 100) : 100;
        return new TeacherEtudiantCoursDTO(
                row.getEtudiantId(),
                row.getNom(),
                row.getPrenom(),
                row.getMatricule(),
                row.getPhotoUrl(),
                row.getNbAbsences() != null ? row.getNbAbsences() : 0L,
                Math.max(0L, absent),
                total,
                Math.max(0, Math.min(100, taux))
        );
    }
}
