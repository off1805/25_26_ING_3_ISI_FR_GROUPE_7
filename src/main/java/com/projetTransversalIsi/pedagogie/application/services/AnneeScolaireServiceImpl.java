package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.AnneeScolaireMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnneeScolaireServiceImpl implements AnneeScolaireService {
    final private AnneeScolaireRepository jpaRepo;
    final private AnneeScolaireMapper mapper;

    @Override
    public AnneeScolaire register(CreateAnneeScolaireRequestDTO command) {
        return jpaRepo.save(mapper.toJpaEntity(command));
    }

    @Override
    public List<AnneeScolaire> getAll() {
        return jpaRepo.findAll();
    }

    @Override
    @Transactional
    public AnneeScolaire activate(Long id) {
        jpaRepo.findActive().ifPresent(current -> {
            current.setActive(false);
            jpaRepo.save(current);
        });
        AnneeScolaire target = jpaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Année scolaire introuvable : " + id));
        target.setActive(true);
        return jpaRepo.save(target);
    }

    @Override
    public Optional<AnneeScolaire> getActive() {
        return jpaRepo.findActive();
    }
}
