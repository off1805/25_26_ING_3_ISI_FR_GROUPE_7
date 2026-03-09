package com.projetTransversalIsi.seance.domain.exceptions;

public class SeanceConflictException extends RuntimeException {
    public SeanceConflictException(String message) {
        super(message);
    }
}