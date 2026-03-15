package com.projetTransversalIsi.Niveau.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class NiveauNotFoundException extends DomainException {
    public NiveauNotFoundException(Long id) {
        super("Niveau non trouvé avec l'identifiant : " + id);
    }
}
