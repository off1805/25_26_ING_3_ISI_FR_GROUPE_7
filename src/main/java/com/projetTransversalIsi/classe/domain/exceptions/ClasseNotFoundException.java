package com.projetTransversalIsi.classe.domain.exceptions;

public class ClasseNotFoundException extends RuntimeException {
    public ClasseNotFoundException(Long id) {
        super("Classe non trouvée avec l'id : " + id);
    }
    public ClasseNotFoundException(String code) {
        super("Classe non trouvée avec le code : " + code);
    }
}
