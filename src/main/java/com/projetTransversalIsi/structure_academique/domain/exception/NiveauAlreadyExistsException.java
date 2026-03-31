package com.projetTransversalIsi.structure_academique.domain.exception;

import com.projetTransversalIsi.common.domain.DomainException;

public class NiveauAlreadyExistsException extends DomainException {
    public NiveauAlreadyExistsException(int ordre, Long filiereId) {
        super("Le niveau avec l'ordre " + ordre + " existe déjà pour la filière " + filiereId);
    }
}
