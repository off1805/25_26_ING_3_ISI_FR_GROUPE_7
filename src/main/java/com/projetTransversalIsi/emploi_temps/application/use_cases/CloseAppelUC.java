package com.projetTransversalIsi.emploi_temps.application.use_cases;

public interface CloseAppelUC {
    // Clôture un appel : toutes les InfoPresenceRow isPresent=null passent à false.
    // La PresenceRow de chaque étudiant a été créée à l'ouverture de l'appel (present=false).
    int execute(Long appelId);
}
