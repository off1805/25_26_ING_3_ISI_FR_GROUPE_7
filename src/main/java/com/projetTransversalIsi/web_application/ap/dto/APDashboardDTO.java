package com.projetTransversalIsi.web_application.ap.dto;

import java.util.List;

public record APDashboardDTO(
        long totalEtudiants,
        long seancesSemaine,
        long justificatifsEnAttente,
        double tauxPresenceGlobal,
        List<String> absencesParClasseLabels,
        List<Long> absencesParClasseData,
        long justificatifsPending,
        long justificatifsApproved,
        long justificatifsRejected,
        List<AbsenceEvolutionDTO> absencesEvolution
) {}
