package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.security.domain.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataUserRepository extends JpaRepository<JpaUserEntity, Long> {
     boolean existsByEmail(String email);
     Optional<JpaUserEntity> findByEmail(String email);

     @Query("SELECT u.password FROM JpaUserEntity u WHERE u.email= :email")
     Optional<String> findPasswordByEmail(@Param("email") String email);
     @Query("SELECT u FROM JpaUserEntity u WHERE u.role.name NOT IN :roles")
     List<JpaUserEntity> findByRoleIdNotIn(@Param("roles")List<String> roles);

    Optional<JpaUserEntity> findByIdAndDeletedFalse(Long id);
    List<JpaUserEntity> findByDeletedFalse();
    List<JpaUserEntity> findByDeletedTrue();

    boolean existsByEmailAndDeletedFalse(String email);

    @Query("SELECT u FROM JpaUserEntity u WHERE u.deleted = true AND u.deletedAt >= :since")
    List<JpaUserEntity> findDeletedSince(@Param("since") LocalDateTime since);
}

