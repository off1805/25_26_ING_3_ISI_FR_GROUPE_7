package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaEmploiTempsEntity;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

// Prédicats JPA Criteria composables pour la recherche paginée d'emplois du temps.
// Chaque méthode retourne null si le critère est absent, ce qui permet à Specification.and()
// d'ignorer automatiquement les prédicats non actifs (comportement natif de Spring Data JPA).
public class JpaEmploiTempsSpec {

    // Filtre sur le statut PAST / ONGOING / UPCOMING.
    public static Specification<JpaEmploiTempsEntity> hasStatus(String status){
        return (root,criteriaQuery,criteriaBuilder)->status==null?null: criteriaBuilder.equal(root.get("status"),status);
    }

    public static Specification<JpaEmploiTempsEntity> isForClasse(Long id){
        return (root,criteriaQuery,criteriaBuilder)->id==null?null: criteriaBuilder.equal(root.get("classeId"),id);
    }

    // startDateAfter/endDateBefore : permet de chercher les emplois commençant après ou finissant avant une date.
    // startDateBeforeOrEqual/endDateAfterOrEqual : inverse, utile pour trouver les emplois actifs à une date.
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

    // Par défaut (deleted=null) on exclut les supprimés (deleted=false) pour les vues normales.
    // Passer deleted=true pour accéder aux archives.
    public static Specification<JpaEmploiTempsEntity> isDeleted(Boolean deleted){
        return (root,criteriaQuery,criteriaBuilder)-> deleted==null ?criteriaBuilder.equal(root.get("deleted"),false): criteriaBuilder.equal(root.get("deleted"),deleted);
    }

}
