package com.projetTransversalIsi.authentification.application.exceptions;

import com.projetTransversalIsi.common.TechnicalException;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message){
        super(message);
    }
}
