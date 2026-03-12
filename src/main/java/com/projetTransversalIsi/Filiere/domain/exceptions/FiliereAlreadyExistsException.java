package com.projetTransversalIsi.Filiere.domain.exceptions;

public class FiliereAlreadyExistsException extends RuntimeException {
    public FiliereAlreadyExistsException(String code) {
        super("Une filière avec le code '" + code + "' existe déjà");
    }
}
