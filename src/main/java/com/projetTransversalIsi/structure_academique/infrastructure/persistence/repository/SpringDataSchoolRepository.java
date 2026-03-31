package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpringDataSchoolRepository extends JpaRepository<JpaSchoolEntity,Long> {
}
