package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.model.CycleStatus;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaCycleEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper.CycleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaCycleRepository implements CycleRepository {

    private final SpringDataCycleRepository jpaRepo;
    private final CycleMapper cycleMapper;

    @Override
    public Cycle save(Cycle cycle) {
        JpaCycleEntity entity = cycleMapper.cycleToJpaCycleEntity(cycle);
        JpaCycleEntity saved = jpaRepo.save(entity);
        log.info("Cycle sauvegardé : id={}, code={}", saved.getId(), saved.getCode());
        return cycleMapper.jpaCycleEntityToCycle(saved);
    }

    @Override
    public Optional<Cycle> findById(Long id) {
        return jpaRepo.findById(id).map(cycleMapper::jpaCycleEntityToCycle);
    }

    @Override
    public Optional<Cycle> findByCode(String code) {
        return jpaRepo.findByCode(code).map(cycleMapper::jpaCycleEntityToCycle);
    }

    @Override
    public List<Cycle> findAll() {
        return jpaRepo.findByDeletedFalse().stream()
                .map(cycleMapper::jpaCycleEntityToCycle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Cycle> findAllActive() {
        return jpaRepo.findByStatusAndDeletedFalse(CycleStatus.ACTIVE).stream()
                .map(cycleMapper::jpaCycleEntityToCycle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Cycle> findAllDeleted() {
        return jpaRepo.findByDeletedTrue().stream()
                .map(cycleMapper::jpaCycleEntityToCycle)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Optional<JpaCycleEntity> entity = jpaRepo.findById(id);
        if (entity.isPresent()) {
            JpaCycleEntity cycleToUpdate = entity.get();
            cycleToUpdate.setDeleted(true);
            cycleToUpdate.setDeletedAt(LocalDateTime.now());
            jpaRepo.save(cycleToUpdate);
            log.info("Cycle marqué comme supprimé : id={}", id);
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepo.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        // Assume existsByName is not explicitly in SpringData repo or add it if needed.
        // For now, let's just use jpaRepo.findAll().stream().anyMatch if not present.
        // Better: I'll check if I should add it to SpringData repo.
        return jpaRepo.findAll().stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    @Override
    public List<Cycle> findAllByDeletedFalse() {
        return findAll();
    }
}
