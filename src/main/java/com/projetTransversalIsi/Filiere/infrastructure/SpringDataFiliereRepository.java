package com.projetTransversalIsi.Filiere.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataFiliereRepository extends JpaRepository<JpaFiliereEntity, Long> {

    Optional<JpaFiliereEntity> findByCode(String code);

    List<JpaFiliereEntity> findByNomContainingIgnoreCase(String nom);

    boolean existsByCode(String code);


    Optional<JpaFiliereEntity> findByIdAndDeletedFalse(Long id);
    List<JpaFiliereEntity> findByDeletedFalse();
    List<JpaFiliereEntity> findByDeletedTrue();
    boolean existsByCodeAndDeletedFalse(String code);

    @Query("SELECT f FROM JpaFiliereEntity f WHERE " +
            "(:code IS NULL OR f.code LIKE %:code%) AND " +
            "(:nom IS NULL OR f.nom LIKE %:nom%)")
    List<JpaFiliereEntity> search(@Param("code") String code, @Param("nom") String nom);
}
