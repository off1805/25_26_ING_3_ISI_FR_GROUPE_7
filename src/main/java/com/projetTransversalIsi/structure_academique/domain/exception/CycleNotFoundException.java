package com.projetTransversalIsi.structure_academique.domain.exception;

public class CycleNotFoundException extends RuntimeException {

    public CycleNotFoundException(Long id) {
        super("Aucun cycle trouvé avec l'ID : " + id);
    }

    public CycleNotFoundException(String code) {
        super("Aucun cycle trouvé avec le code : " + code);
    }
}
