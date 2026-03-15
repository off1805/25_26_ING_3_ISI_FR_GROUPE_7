package com.projetTransversalIsi.classe.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class ClasseNotFoundException extends DomainException {
    public ClasseNotFoundException(Long id) {
        super("Classe non trouvée avec l'id : " + id);
    }
    public ClasseNotFoundException(String code) {
        super("Classe non trouvée avec le code : " + code);
    }
}
