package com.projetTransversalIsi.ue.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class UeNotFoundException extends DomainException {
    public UeNotFoundException(Long id) {
        super("UE identified by id < " + id + " > not found.");
    }

    public UeNotFoundException(String code) {
        super("UE identified by code < " + code + " > not found.");
    }
}
