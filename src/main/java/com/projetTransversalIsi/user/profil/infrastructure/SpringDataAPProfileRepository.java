package com.projetTransversalIsi.user.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAPProfileRepository extends JpaRepository<JpaAPProfileEntity, Long> {

    Optional<JpaAPProfileEntity> findByUserId(Long userId);
}
