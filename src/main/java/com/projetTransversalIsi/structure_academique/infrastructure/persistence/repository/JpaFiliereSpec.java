package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaFiliereEntity;

import org.springframework.data.jpa.domain.Specification;

public class JpaFiliereSpec {

    public static Specification<JpaFiliereEntity> inCycle(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> id == null ? null : criteriaBuilder.equal(root.join("cycle").get("id"), id);
    }

}
