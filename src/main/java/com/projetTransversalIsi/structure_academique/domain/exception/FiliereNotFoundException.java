package com.projetTransversalIsi.structure_academique.domain.exception;

public class FiliereNotFoundException extends RuntimeException {
    public FiliereNotFoundException(Long id) {
        super("Filière non trouvée avec l'ID: " + id);
    }

    public FiliereNotFoundException(String code) {
        super("Filière non trouvée avec le code: " + code);
    }
}
