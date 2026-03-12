package com.projetTransversalIsi.cycle.infrastructure;

import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.CycleStatus;
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
    public boolean cycleAlreadyExists(String code) {
        return jpaRepo.existsByCode(code);
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
    public List<Cycle> findDeletedSince(LocalDateTime since) {
        return jpaRepo.findDeletedSince(since).stream()
                .map(cycleMapper::jpaCycleEntityToCycle)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Cycle cycle) {
        cycle.delete();
        save(cycle);
    }
}
