package com.projetTransversalIsi.specialite.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class SpecialiteNotFoundException extends DomainException {

    public SpecialiteNotFoundException(Long id) {
        super("Spécialité identifiée par l'id < " + id + " > introuvable.");
    }

    public SpecialiteNotFoundException(String code) {
        super("Spécialité identifiée par le code < " + code + " > introuvable.");
    }
}
