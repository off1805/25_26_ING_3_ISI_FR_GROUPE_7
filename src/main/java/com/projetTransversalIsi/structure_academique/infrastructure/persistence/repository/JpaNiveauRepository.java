package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.application.dto.SearchNiveauRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper.NiveauMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaNiveauRepository implements NiveauRepository {

    private final SpringDataNiveauRepository jpaRepo;
    private final NiveauMapper mapper;

    @Override
    public Niveau save(Niveau niveau) {
        JpaNiveauEntity entity = mapper.toEntity(niveau);
        JpaNiveauEntity savedEntity = jpaRepo.save(entity);
        log.info("Niveau sauvegardé: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Niveau> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Niveau> findAll(SearchNiveauRequestDTO criteria) {
        Specification<JpaNiveauEntity> spec = Specification
                .where(JpaNiveauSpec.hasOrdre(criteria.ordre()))
                .and(JpaNiveauSpec.hasDescriptionLike(criteria.description()));

        if (!criteria.includeDeleted()) {
            spec = spec.and(JpaNiveauSpec.isDeleted(false));
        }

        return jpaRepo.findAll(spec).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Niveau niveau) {
        jpaRepo.deleteById(niveau.getId());
    }

    @Override
    public Optional<Niveau> findActiveById(Long id) {
        return jpaRepo.findByIdAndDeletedFalse(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrdreAndFiliereId(int ordre, Long filiereId) {
        return jpaRepo.existsByOrdreAndFiliereId(ordre, filiereId);
    }

    @Override
    public boolean existsByOrdreAndFiliereIdAndDeletedFalse(int ordre, Long filiereId) {
        return jpaRepo.existsByOrdreAndFiliereIdAndDeletedFalse(ordre, filiereId);
    }

    @Override
    public List<Niveau> findByFiliereId(Long filiereId) {
        return jpaRepo.findByFiliereIdAndDeletedFalse(filiereId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
