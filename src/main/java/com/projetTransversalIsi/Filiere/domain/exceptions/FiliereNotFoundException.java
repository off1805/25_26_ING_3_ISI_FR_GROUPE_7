package com.projetTransversalIsi.Filiere.domain.exceptions;

public class FiliereNotFoundException extends RuntimeException {
    public FiliereNotFoundException(Long id) {
        super("Filière non trouvée avec l'ID: " + id);
    }

    public FiliereNotFoundException(String code) {
        super("Filière non trouvée avec le code: " + code);
    }
}
