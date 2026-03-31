package com.projetTransversalIsi.emploi_temps.domain.exceptions;

public class SeanceNotFoundException extends RuntimeException {
    public SeanceNotFoundException(Long id) {
        super("Séance non trouvée avec l'ID: " + id);
    }
}
