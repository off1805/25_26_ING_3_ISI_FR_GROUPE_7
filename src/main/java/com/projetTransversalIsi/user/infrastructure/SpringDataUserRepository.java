package com.projetTransversalIsi.user.infrastructure;

import com.projetTransversalIsi.Filiere.infrastructure.JpaFiliereEntity;
import com.projetTransversalIsi.security.domain.EnumRole;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.enums.UserStatus;
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

    @Query("SELECT f FROM JpaUserEntity f WHERE " +
            "(:status IS NULL OR f.status = :status ) AND " +
            "(:email IS NULL OR f.email LIKE %:email%) AND "+
            "(:role IS NULL OR f.role.name = :role ) AND "+
            "( f.deleted = :deleted)")
    List<JpaUserEntity> search(@Param("status") String userStatus, @Param("email") String email,@Param("role") String role, @Param("deleted") boolean deleted);

}

