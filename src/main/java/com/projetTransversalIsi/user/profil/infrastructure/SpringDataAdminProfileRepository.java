package com.projetTransversalIsi.user.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAdminProfileRepository extends JpaRepository<JpaAdminProfileEntity, Long> {
}
