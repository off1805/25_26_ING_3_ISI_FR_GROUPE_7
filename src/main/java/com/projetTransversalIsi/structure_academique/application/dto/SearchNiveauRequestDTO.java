package com.projetTransversalIsi.structure_academique.application.dto;

public record SearchNiveauRequestDTO(
        Integer ordre,
        String description,
        boolean includeDeleted
) {}
