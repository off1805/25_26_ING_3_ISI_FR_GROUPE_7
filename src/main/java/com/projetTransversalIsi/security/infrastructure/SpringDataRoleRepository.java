package com.projetTransversalIsi.security.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataRoleRepository extends JpaRepository<JpaRoleEntity, String> {

    @Query("SELECT COUNT(p) FROM JpaRoleEntity r JOIN r.permissions p")
    Long countPermissionLinks();
}
