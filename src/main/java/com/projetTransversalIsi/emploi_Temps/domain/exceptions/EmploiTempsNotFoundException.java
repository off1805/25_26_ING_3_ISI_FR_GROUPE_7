package com.projetTransversalIsi.emploi_Temps.domain.exceptions;



public class EmploiTempsNotFoundException extends RuntimeException {
    public EmploiTempsNotFoundException(Long id) {
        super("Emploi du temps non trouvé avec l'ID: " + id);
    }
}