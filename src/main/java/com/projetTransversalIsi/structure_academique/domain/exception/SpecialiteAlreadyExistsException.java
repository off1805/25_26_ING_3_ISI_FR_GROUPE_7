package com.projetTransversalIsi.structure_academique.domain.exception;

import com.projetTransversalIsi.common.domain.DomainException;

public class SpecialiteAlreadyExistsException extends DomainException {

    public SpecialiteAlreadyExistsException(String code) {
        super("Une spécialité avec le code < " + code + " > existe déjà.");
    }
}
