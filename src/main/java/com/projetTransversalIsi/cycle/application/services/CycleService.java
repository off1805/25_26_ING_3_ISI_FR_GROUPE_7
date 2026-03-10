package com.projetTransversalIsi.cycle.application.services;

import com.projetTransversalIsi.cycle.domain.Cycle;

import java.util.List;
import java.util.Optional;

public interface CycleService {

    Optional<Cycle> findCycleById(Long id);

    List<Cycle> findAllActiveCycles();

    void deleteCycle(Long id);
}
