package com.projetTransversalIsi.cycle.domain.exceptions;

public class CycleNotFoundException extends RuntimeException {

    public CycleNotFoundException(Long id) {
        super("Aucun cycle trouvé avec l'ID : " + id);
    }

    public CycleNotFoundException(String code) {
        super("Aucun cycle trouvé avec le code : " + code);
    }
}
