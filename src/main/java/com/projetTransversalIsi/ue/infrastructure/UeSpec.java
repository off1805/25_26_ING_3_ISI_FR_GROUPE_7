package com.projetTransversalIsi.ue.infrastructure;

import org.springframework.data.jpa.domain.Specification;

public class UeSpec {

    private UeSpec() {
    }

    public static Specification<JpaUeEntity> hasLibelle(String libelle) {
        return (root, query, cb) -> {
            if (libelle == null || libelle.isBlank())
                return null;
            return cb.like(cb.lower(root.get("libelle")), "%" + libelle.toLowerCase() + "%");
        };
    }

    public static Specification<JpaUeEntity> hasCode(String code) {
        return (root, query, cb) -> {
            if (code == null || code.isBlank())
                return null;
            return cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%");
        };
    }

    public static Specification<JpaUeEntity> isDeleted(Boolean deleted) {
        return (root, query, cb) -> {
            if (deleted == null)
                return null;
            return cb.equal(root.get("isDeleted"), deleted);
        };
    }
}
