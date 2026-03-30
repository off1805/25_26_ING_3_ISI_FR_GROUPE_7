package com.projetTransversalIsi.structure_academique.application.dto;

import com.projetTransversalIsi.structure_academique.domain.model.CycleStatus;
import jakarta.validation.constraints.NotNull;

public record ModifyCycleStatusDTO(@NotNull CycleStatus status) {}
