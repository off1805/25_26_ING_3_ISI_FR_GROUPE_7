package com.projetTransversalIsi.structure_academique.domain.exception;

public class FiliereAlreadyExistsException extends RuntimeException {
    public FiliereAlreadyExistsException(String code) {
        super("Une filière avec le code '" + code + "' existe déjà");
    }
}
