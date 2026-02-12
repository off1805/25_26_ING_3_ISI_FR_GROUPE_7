package com.projetTransversalIsi.authentification.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRefreshTokenRepository extends JpaRepository<JpaRefreshTokenEntity,String>{

}
