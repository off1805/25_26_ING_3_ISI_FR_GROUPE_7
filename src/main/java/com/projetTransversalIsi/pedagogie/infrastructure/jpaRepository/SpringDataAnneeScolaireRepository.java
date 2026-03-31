package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;


import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaAnneeScolaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAnneeScolaireRepository extends JpaRepository<JpaAnneeScolaireEntity,Long>{

}
