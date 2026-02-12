package com.projetTransversalIsi.common;

public abstract class TechnicalException extends RuntimeException {
    public TechnicalException(String message) {
        super(message);
    }
}
