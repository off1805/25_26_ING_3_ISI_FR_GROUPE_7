package com.projetTransversalIsi.Filiere.infrastructure;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class JpaFiliereSpec {

    public static Specification<JpaFiliereEntity> inCycle(Long id) {
        return (root, criteriaQuery, criteriaBuilder) ->id == null ? null : criteriaBuilder.equal(root.join("cycle").get("id"), id);
    }

}
