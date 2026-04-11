package com.projetTransversalIsi.user.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataProfileRepository<T extends JpaProfileEntity > extends JpaRepository<T,Long> {
}
