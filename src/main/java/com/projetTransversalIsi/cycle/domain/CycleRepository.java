package com.projetTransversalIsi.cycle.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CycleRepository {

    Cycle save(Cycle cycle);

    boolean cycleAlreadyExists(String code);

    Optional<Cycle> findById(Long id);

    Optional<Cycle> findByCode(String code);

    List<Cycle> findAll();

    List<Cycle> findAllActive();

    List<Cycle> findAllDeleted();

    List<Cycle> findDeletedSince(LocalDateTime since);

    void delete(Cycle cycle);
}
