package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.user.dto.ModifyUserStatusDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class JpaUserSpec {

    public static Specification<JpaUserEntity> hasStatus(String status) {
        return (root, criteriaQuery, criteriaBuilder) ->status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<JpaUserEntity> hasEmailLike(String email) {
        return (root, criteriaQuery, criteriaBuilder) ->{
            if (email == null || email.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
            );
        };
    }

    public static Specification<JpaUserEntity> hasAnyRole(List<String> roles) {
        return (root, criteriaQuery, criteriaBuilder) ->{
            if(roles==null || roles.isEmpty()) return null;
            CriteriaBuilder.In<String> inClause= criteriaBuilder.in(root.join("role").get("name"));
            roles.forEach(inClause::value);
            return inClause;
        };
    }

    public static Specification<JpaUserEntity> isFromClass(Long classId){
        return ((root, query, criteriaBuilder) -> classId == null?null:criteriaBuilder.equal(root.join("profile").join("classe").get("id"),classId));
    }

    public static Specification<JpaUserEntity> isDeleted(Boolean deleted){
        return ((root, query, criteriaBuilder) -> deleted == null?null:criteriaBuilder.equal(root.get("deleted"),deleted));
    }
}
