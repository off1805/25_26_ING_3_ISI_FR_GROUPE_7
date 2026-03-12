package com.projetTransversalIsi.ue.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class UeAlreadyExistsException extends DomainException {
    public UeAlreadyExistsException(String code) {
        super("UE with code <" + code + "> already exists.");
    }
}
