package com.projetTransversalIsi.emploi_temps.application.dto;

public record TeacherCoursCardDTO(
        Long    offreUeId,
        String  libelle,
        String  code,
        String  couleur,
        Integer semestre,
        Integer credit,
        Integer volumeHoraireTotal,
        Long    classeId,
        String  classeCode,
        long    nbEtudiants,
        long    nbSeancesFaites
) {
    public static TeacherCoursCardDTO from(TeacherCoursCardRow row) {
        return new TeacherCoursCardDTO(
                row.getOffreUeId(),
                row.getLibelle(),
                row.getCode(),
                row.getCouleur(),
                row.getSemestre(),
                row.getCredit(),
                row.getVolumeHoraireTotal(),
                row.getClasseId(),
                row.getClasseCode(),
                row.getNbEtudiants()     != null ? row.getNbEtudiants()     : 0L,
                row.getNbSeancesFaites() != null ? row.getNbSeancesFaites() : 0L
        );
    }
}
