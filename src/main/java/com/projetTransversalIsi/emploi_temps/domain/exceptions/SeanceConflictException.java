package com.projetTransversalIsi.emploi_temps.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class SeanceConflictException extends DomainException {
    public SeanceConflictException(String message) {
        super(message);
    }
}
