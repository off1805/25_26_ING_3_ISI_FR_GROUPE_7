package com.projetTransversalIsi.emploi_temps.application.dto;

public record TeacherCoursStatsDTO(
        long   totalSeances,
        long   totalMinutesProgrammes,
        long   nbEtudiants,
        int    tauxPresenceGlobal,
        long   nbPresencesTotal,
        long   nbAbsencesTotal
) {}
