package com.projetTransversalIsi.Niveau.infrastructure;

import org.springframework.data.jpa.domain.Specification;

public class JpaNiveauSpec {

    public static Specification<JpaNiveauEntity> hasOrdre(Integer ordre) {
        return (root, query, cb) -> ordre == null ? null : cb.equal(root.get("ordre"), ordre);
    }

    public static Specification<JpaNiveauEntity> hasDescriptionLike(String description) {
        return (root, query, cb) -> {
            if (description == null || description.isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
        };
    }

    public static Specification<JpaNiveauEntity> isDeleted(Boolean deleted) {
        return (root, query, cb) -> deleted == null ? null : cb.equal(root.get("deleted"), deleted);
    }
    
    public static Specification<JpaNiveauEntity> hasFiliereId(Long filiereId) {
        return (root, query, cb) -> filiereId == null ? null : cb.equal(root.get("filiere").get("id"), filiereId);
    }
}
