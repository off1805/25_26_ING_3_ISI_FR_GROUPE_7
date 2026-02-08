package com.projetTransversalIsi.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUserRepository extends JpaRepository<JpaUserEntity, Long> {
     boolean existsByEmail(String email);
}

