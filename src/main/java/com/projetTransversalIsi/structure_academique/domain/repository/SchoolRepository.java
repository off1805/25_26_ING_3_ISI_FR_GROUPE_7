package com.projetTransversalIsi.structure_academique.domain.repository;

import com.projetTransversalIsi.structure_academique.domain.model.School;

import java.util.Optional;

public interface SchoolRepository {

    Optional<School> findById(Long id);

    School save(School school);

    boolean existsById(Long id);
}
