package com.projetTransversalIsi.Niveau.domain.exceptions;

public class NiveauNotFoundException extends RuntimeException {
    public NiveauNotFoundException(Long id) {
        super("Niveau non trouvé avec l'identifiant : " + id);
    }
}
