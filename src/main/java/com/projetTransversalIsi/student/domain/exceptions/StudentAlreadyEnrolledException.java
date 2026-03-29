package com.projetTransversalIsi.student.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class StudentAlreadyEnrolledException extends DomainException {
    public StudentAlreadyEnrolledException(String email) {
        super("L'étudiant <" + email + "> est déjà inscrit dans une classe.");
    }
}
