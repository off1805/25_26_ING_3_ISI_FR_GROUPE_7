package com.projetTransversalIsi.pedagogie.domain.exceptions;

public class OffreUeNotFoundException extends RuntimeException {

    public OffreUeNotFoundException(Long id) {
        super("OffreUe introuvable avec l'identifiant : " + id);
    }
}
