package com.projetTransversalIsi.pedagogie.domain.model;

/**
 * Représente l'affectation d'un enseignant à une classe pour une offre d'UE.
 */
public record OffreUeAssignment(Long enseignantId, Long classeId) {
}
