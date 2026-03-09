package com.projetTransversalIsi.classe.domain.exception;

import com.projetTransversalIsi.common.domain.DomainException;

public class ClasseAlreadyExistsException extends DomainException {
    public ClasseAlreadyExistsException(String nom) {
        super("La classe <" + nom + "> existe déjà.");
    }
}
