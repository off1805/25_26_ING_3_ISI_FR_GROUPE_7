package com.projetTransversalIsi.emploi_temps.application.use_cases;

public interface CloseAllPendingAppelsUC {
    // Clôture tous les appels ayant encore des InfoPresenceRow en attente (isPresent=null)
    // pour cette liste de présence. Retourne le nombre de créneaux passés à "absent".
    int execute(Long presenceListId);
}
