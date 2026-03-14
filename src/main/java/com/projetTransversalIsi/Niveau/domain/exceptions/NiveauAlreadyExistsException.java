package com.projetTransversalIsi.Niveau.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class NiveauAlreadyExistsException extends DomainException {
    public NiveauAlreadyExistsException(int ordre, Long filiereId) {
        super("Le niveau avec l'ordre " + ordre + " existe déjà pour la filière " + filiereId);
    }
}
