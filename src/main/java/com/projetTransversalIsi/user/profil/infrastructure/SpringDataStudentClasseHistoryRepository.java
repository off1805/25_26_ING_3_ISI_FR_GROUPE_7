package com.projetTransversalIsi.user.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataStudentClasseHistoryRepository extends JpaRepository<JpaStudentClasseHistoryEntity, Long> {
    Optional<JpaStudentClasseHistoryEntity> findByStudentIdAndDateFinIsNull(Long studentId);
    List<JpaStudentClasseHistoryEntity> findByStudentIdOrderByDateDebutDesc(Long studentId);
}
