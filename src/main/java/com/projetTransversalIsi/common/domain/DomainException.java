package com.projetTransversalIsi.common.domain;

public abstract class DomainException extends RuntimeException {
    protected DomainException(String message){
        super(message);
    }
}
