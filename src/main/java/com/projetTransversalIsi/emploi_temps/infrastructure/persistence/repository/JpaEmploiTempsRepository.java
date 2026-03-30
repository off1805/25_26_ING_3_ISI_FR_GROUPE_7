package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.EmploiTemps;
import com.projetTransversalIsi.emploi_temps.domain.repository.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaEmploiTempsEntity;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.EmploiTempsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaEmploiTempsRepository implements EmploiTempsRepository {

    private final SpringDataEmploiTempsRepository jpaRepo;
    private final EmploiTempsMapper mapper;

    @Override
    public EmploiTemps save(EmploiTemps emploiTemps) {
        JpaEmploiTempsEntity entity = mapper.toEntity(emploiTemps);
        JpaEmploiTempsEntity savedEntity = jpaRepo.save(entity);
        log.info("Emploi du temps sauvegardé id={}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<EmploiTemps> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<EmploiTemps> findAll() {
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(EmploiTemps emploiTemps) {
        jpaRepo.deleteById(emploiTemps.getId());
    }

    @Override
    public Optional<EmploiTemps> findActiveById(Long id) {
        return jpaRepo.findByIdAndDeletedFalse(id).map(mapper::toDomain);
    }

    @Override
    public List<EmploiTemps> findAllActive() {
        return jpaRepo.findByDeletedFalse().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmploiTemps> findAllDeleted() {
        return jpaRepo.findByDeletedTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmploiTemps> findByClasseId(Long classeId) {
        return jpaRepo.findByClasseId(classeId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmploiTemps> findByPeriode(LocalDate date) {
        return jpaRepo.findByPeriode(date).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmploiTemps> findBySemaine(Integer semaine) {
        return jpaRepo.findBySemaine(semaine).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsEmploiForPeriode(Long classeId, LocalDate dateDebut, LocalDate dateFin) {
        return jpaRepo.existsEmploiForPeriode(classeId, dateDebut, dateFin);
    }
}
