package com.projetTransversalIsi.emploi_Temps.domain.exceptions;


public class EmploiTempsConflictException extends RuntimeException {
    public EmploiTempsConflictException(String message) {
        super(message);
    }
}