package com.projetTransversalIsi.pedagogie.domain.exceptions;

public class OffreUeAlreadyExistsException extends RuntimeException {

    public OffreUeAlreadyExistsException(Long ueId, Long anneeScolaireId) {
        super("Une offre existe déjà pour l'UE " + ueId
                + " et l'année scolaire " + anneeScolaireId);
    }
}
