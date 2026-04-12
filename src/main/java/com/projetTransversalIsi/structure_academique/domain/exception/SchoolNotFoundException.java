package com.projetTransversalIsi.structure_academique.domain.exception;

public class SchoolNotFoundException extends RuntimeException {

    public SchoolNotFoundException(Long id) {
        super("Aucune école trouvée avec l'ID : " + id);
    }
}
