package com.projetTransversalIsi.structure_academique.application.dto;

public record SearchFiliereRequestDTO(
    String code,
    String nom,
    Long cycleId,
    boolean includeDeleted
) {}
