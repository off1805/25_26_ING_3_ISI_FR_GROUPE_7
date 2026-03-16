package com.projetTransversalIsi.seance.infrastructure;

import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaSeanceRepository implements SeanceRepository {

    private final SpringDataSeanceRepository jpaRepo;
    private final SeanceMapper mapper;

    @Override
    public Seance save(Seance seance) {
        JpaSeanceEntity entity = mapper.toEntity(seance);
        JpaSeanceEntity savedEntity = jpaRepo.save(entity);
        log.info("Séance sauvegardée: {}", savedEntity.getDateSeance());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Seance> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Seance> findAll() {
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Seance seance) {
        jpaRepo.deleteById(seance.getId());
    }

    @Override
    public Optional<Seance> findActiveById(Long id) {
        return jpaRepo.findByIdAndDeletedFalse(id).map(mapper::toDomain);
    }

    @Override
    public List<Seance> findAllActive() {
        return jpaRepo.findByDeletedFalse().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seance> findAllDeleted() {
        return jpaRepo.findByDeletedTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seance> findByDate(LocalDate date) {
        return jpaRepo.findByDateSeance(date).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seance> findByEnseignantId(Long enseignantId) {
        return jpaRepo.findByEnseignantId(enseignantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seance> findByCoursId(Long coursId) {
        return jpaRepo.findByCoursId(coursId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsConflict(Long enseignantId, LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        return jpaRepo.existsConflictForEnseignant(enseignantId, date, heureDebut, heureFin);
    }

}
