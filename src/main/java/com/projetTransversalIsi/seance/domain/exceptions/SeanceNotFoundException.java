package com.projetTransversalIsi.seance.domain.exceptions;

public class SeanceNotFoundException extends RuntimeException {
    public SeanceNotFoundException(Long id) {
        super("Séance non trouvée avec l'ID: " + id);
    }
}