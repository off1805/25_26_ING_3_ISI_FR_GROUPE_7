package com.projetTransversalIsi.seance.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class SeanceConflictException extends DomainException {
    public SeanceConflictException(String message) {
        super(message);
    }
}

