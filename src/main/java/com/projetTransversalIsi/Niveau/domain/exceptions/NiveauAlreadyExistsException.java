package com.projetTransversalIsi.Niveau.domain.exceptions;

public class NiveauAlreadyExistsException extends RuntimeException {
    public NiveauAlreadyExistsException(int ordre, Long filiereId) {
        super("Le niveau avec l'ordre " + ordre + " existe déjà pour la filière " + filiereId);
    }
}
