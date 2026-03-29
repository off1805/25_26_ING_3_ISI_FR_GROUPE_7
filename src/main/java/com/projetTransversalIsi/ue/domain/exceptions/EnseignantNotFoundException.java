package com.projetTransversalIsi.ue.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class EnseignantNotFoundException extends DomainException {
    public EnseignantNotFoundException(Long id) {
        super("Enseignant identified by id <" + id + "> not found.");
    }
}
