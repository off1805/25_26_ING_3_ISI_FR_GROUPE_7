package com.projetTransversalIsi.structure_academique.domain.exception;

public class CycleAlreadyExistsException extends RuntimeException {

    public CycleAlreadyExistsException(String code) {
        super("Un cycle avec le code '" + code + "' existe déjà.");
    }
}
