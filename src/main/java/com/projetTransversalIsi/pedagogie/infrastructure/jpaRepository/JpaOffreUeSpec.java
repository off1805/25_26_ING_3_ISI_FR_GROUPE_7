package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaOffreUeEntity;
import org.springframework.data.jpa.domain.Specification;

public class JpaOffreUeSpec {

    public static Specification<JpaOffreUeEntity> hasSpecialiteId(Long specialiteId) {
        return (root, query, cb) ->
            specialiteId == null ? null : cb.equal(root.get("specialiteId"), specialiteId);
    }

    public static Specification<JpaOffreUeEntity> hasAnneeScolaireId(Long anneeScolaireId) {
        return (root, query, cb) ->
            anneeScolaireId == null ? null : cb.equal(root.get("anneeScolaire").get("id"), anneeScolaireId);
    }

    public static Specification<JpaOffreUeEntity> hasSemestre(Integer semestre) {
        return (root, query, cb) ->
            semestre == null ? null : cb.equal(root.get("semestre"), semestre);
    }

    public static Specification<JpaOffreUeEntity> hasLibelleLike(String libelle) {
        return (root, query, cb) -> {
            if (libelle == null || libelle.isBlank()) return null;
            return cb.like(cb.lower(root.get("libelle")), "%" + libelle.toLowerCase() + "%");
        };
    }
}
