package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.migration.application.use_cases.ExecuteMigrationsUC;
import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.AnneeScolaireMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaSemestreEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataSemestreRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataNiveauRepository;
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
    final private SpringDataNiveauRepository niveauRepository;
    final private SpringDataSemestreRepository semestreRepository;
    final private ExecuteMigrationsUC executeMigrationsUC;

    @Override
    @Transactional
    public AnneeScolaire register(CreateAnneeScolaireRequestDTO command) {
        AnneeScolaire saved = jpaRepo.save(mapper.toJpaEntity(command));

        List<JpaNiveauEntity> niveaux = niveauRepository.findAll();
        for (JpaNiveauEntity niveau : niveaux) {
            if (!semestreRepository.existsByAnneeScolaireIdAndNiveauIdAndNumero(saved.getId(), niveau.getId(), 1)) {
                JpaSemestreEntity s1 = new JpaSemestreEntity();
                s1.setNumero(1);
                s1.setLibelle("Semestre 1");
                s1.setAnneeScolaireId(saved.getId());
                s1.setNiveauId(niveau.getId());
                semestreRepository.save(s1);
            }

            if (!semestreRepository.existsByAnneeScolaireIdAndNiveauIdAndNumero(saved.getId(), niveau.getId(), 2)) {
                JpaSemestreEntity s2 = new JpaSemestreEntity();
                s2.setNumero(2);
                s2.setLibelle("Semestre 2");
                s2.setAnneeScolaireId(saved.getId());
                s2.setNiveauId(niveau.getId());
                semestreRepository.save(s2);
            }
        }

        return saved;
    }

    @Override
    public List<AnneeScolaire> getAll() {
        List<AnneeScolaire> all = jpaRepo.findAll();
        return jpaRepo.findActive()
                .map(active -> all.stream()
                        .filter(a -> a.getAnneeDebut() >= active.getAnneeDebut())
                        .toList())
                .orElse(all);
    }

    @Override
    @Transactional
    public AnneeScolaire activate(Long id) {
        AnneeScolaire target = jpaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Année scolaire introuvable : " + id));

        Optional<AnneeScolaire> currentOpt = jpaRepo.findActive();
        if (currentOpt.isPresent()) {
            AnneeScolaire current = currentOpt.get();
            if (target.getAnneeDebut() < current.getAnneeDebut()) {
                throw new IllegalStateException("Cette année scolaire est déjà terminée et ne peut pas être réactivée.");
            }
            if (!current.getId().equals(target.getId())) {
                current.setActive(false);
                jpaRepo.save(current);
            }
        }

        target.setActive(true);
        AnneeScolaire saved = jpaRepo.save(target);

        executeMigrationsUC.execute();

        return saved;
    }

    @Override
    public Optional<AnneeScolaire> getActive() {
        return jpaRepo.findActive();
    }
}
