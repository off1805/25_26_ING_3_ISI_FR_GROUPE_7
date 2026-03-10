package com.projetTransversalIsi.cycle.domain.exceptions;

public class CycleAlreadyExistsException extends RuntimeException {

    public CycleAlreadyExistsException(String code) {
        super("Un cycle avec le code '" + code + "' existe déjà.");
    }
}
