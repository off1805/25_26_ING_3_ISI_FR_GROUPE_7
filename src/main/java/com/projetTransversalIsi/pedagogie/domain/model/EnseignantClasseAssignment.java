package com.projetTransversalIsi.pedagogie.domain.model;

/**
 * Affectation d'un enseignant à une classe pour une OffreUe donnée.
 * Représente le fait qu'un enseignant donne cours dans une classe précise.
 */
public record EnseignantClasseAssignment(Long enseignantId, Long classeId) {}
