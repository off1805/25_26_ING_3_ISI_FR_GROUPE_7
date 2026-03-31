package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.AnneeScolaireMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnneeScolaireServiceImpl implements AnneeScolaireService{
    final private AnneeScolaireRepository jpaRepo;
    final private AnneeScolaireMapper mapper;

    @Override
    public AnneeScolaire register(CreateAnneeScolaireRequestDTO command){
        System.out.println(command);
        return jpaRepo.save(mapper.toJpaEntity(command));
    }
}
