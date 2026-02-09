package com.projetTransversalIsi.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProfileRepository<T extends JpaProfileEntity > extends JpaRepository<T,Long> {
}
