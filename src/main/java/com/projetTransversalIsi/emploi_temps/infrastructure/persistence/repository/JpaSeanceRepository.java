package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaSeanceEntity;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.SeanceMapper;
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

    // -------------------------------------------------------------------------
    // Nouvelles méthodes — use cases professeur
    // -------------------------------------------------------------------------

    @Override
    public List<Seance> findByEnseignantIdAndDate(Long enseignantId, LocalDate date) {
        return jpaRepo.findByEnseignantIdAndDateSeance(enseignantId, date).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seance> findByEnseignantIdAndDateBetween(Long enseignantId,
                                                          LocalDate debut,
                                                          LocalDate fin) {
        return jpaRepo.findByEnseignantIdAndDateSeanceBetween(enseignantId, debut, fin).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
