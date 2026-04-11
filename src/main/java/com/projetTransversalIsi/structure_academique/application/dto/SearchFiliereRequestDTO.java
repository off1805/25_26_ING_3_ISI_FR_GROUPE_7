package com.projetTransversalIsi.structure_academique.application.dto;

import jakarta.annotation.Nullable;

public record SearchFiliereRequestDTO(
    String code,
    String nom,
    Long cycleId,
    Boolean includeDeleted
) {}
