package com.projetTransversalIsi.student.application.use_cases;

import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class JpaStudentSpec {

        public static Specification<JpaStudentProfileEntity> isInClass(Long classId) {
            return (root, criteriaQuery, criteriaBuilder) ->classId == null ? null : criteriaBuilder.equal(root.join("classe").get("id"), classId);
        }
}
