package com.projetTransversalIsi.cycle.application.dto;

import com.projetTransversalIsi.cycle.domain.CycleStatus;
import jakarta.validation.constraints.NotNull;

public record ModifyCycleStatusDTO(@NotNull CycleStatus status) {}
