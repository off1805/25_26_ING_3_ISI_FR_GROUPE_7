package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaEmploiTempsEntity;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class JpaEmploiTempsSpec {
    public static Specification<JpaEmploiTempsEntity> hasStatus(String status){
        return (root,criteriaQuery,criteriaBuilder)->status==null?null: criteriaBuilder.equal(root.get("status"),status);
    }

    public static Specification<JpaEmploiTempsEntity> isForClasse(Long id){
        return (root,criteriaQuery,criteriaBuilder)->id==null?null: criteriaBuilder.equal(root.get("classe_id"),id);
    }

    public static Specification<JpaEmploiTempsEntity> startDateAfter(LocalDate date){
        return (root,criteriaQuery,criteriaBuilder)->date==null?null: criteriaBuilder.greaterThan(root.get("dateDebut"),date);
    }

    public static Specification<JpaEmploiTempsEntity> endDateBefore(LocalDate date){
        return (root,criteriaQuery,criteriaBuilder)->date==null?null: criteriaBuilder.lessThan(root.get("dateFin"),date);
    }

    public static Specification<JpaEmploiTempsEntity> startDateBeforeOrEqual(LocalDate date){
        return (root,criteriaQuery,criteriaBuilder)->date==null?null: criteriaBuilder.lessThanOrEqualTo(root.get("dateDebut"),date);
    }

    public static Specification<JpaEmploiTempsEntity> endDateAfterOrEqual(LocalDate date){
        return (root,criteriaQuery,criteriaBuilder)->date==null?null: criteriaBuilder.greaterThanOrEqualTo(root.get("dateFin"),date);
    }

    public static Specification<JpaEmploiTempsEntity> isDeleted(Boolean deleted){
        return (root,criteriaQuery,criteriaBuilder)-> deleted==null ?criteriaBuilder.equal(root.get("deleted"),false): criteriaBuilder.equal(root.get("deleted"),deleted);
    }

}
