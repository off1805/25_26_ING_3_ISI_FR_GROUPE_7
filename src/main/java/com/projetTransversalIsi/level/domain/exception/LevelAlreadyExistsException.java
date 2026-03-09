package com.projetTransversalIsi.level.domain.exception;

import com.projetTransversalIsi.common.domain.DomainException;

public class LevelAlreadyExistsException extends DomainException {
    public LevelAlreadyExistsException(String nom) {
        super("Le niveau <" + nom + "> existe déjà.");
    }
}
