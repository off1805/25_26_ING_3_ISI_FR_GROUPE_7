package com.projetTransversalIsi.specialite.infrastructure;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaSpecialiteRepository implements SpecialiteRepository {

    private final SpringDataSpecialiteRepository jpaRepo;
    private final SpecialiteMapper specialiteMapper;

    @Override
    public Specialite save(Specialite specialite) {
        JpaSpecialiteEntity entity = specialiteMapper.specialiteToJpaEntity(specialite);
        JpaSpecialiteEntity saved = jpaRepo.save(entity);
        log.info("Spécialité sauvegardée : id={}, code={}", saved.getId(), saved.getCode());
        return specialiteMapper.jpaEntityToSpecialite(saved);
    }

    @Override
    public Optional<Specialite> findById(Long id) {
        return jpaRepo.findById(id).map(specialiteMapper::jpaEntityToSpecialite);
    }

    @Override
    public Optional<Specialite> findByCode(String code) {
        return jpaRepo.findByCode(code).map(specialiteMapper::jpaEntityToSpecialite);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepo.existsByCode(code);
    }

    @Override
    public List<Specialite> findAll() {
        return jpaRepo.findAll().stream()
                .map(specialiteMapper::jpaEntityToSpecialite)
                .collect(Collectors.toList());
    }

    @Override
    public List<Specialite> findAllActive() {
        return jpaRepo.findByActiveTrue().stream()
                .map(specialiteMapper::jpaEntityToSpecialite)
                .collect(Collectors.toList());
    }

    @Override
    public List<Specialite> findByNiveauId(Long niveauId) {
        return jpaRepo.findByNiveauId(niveauId).stream()
                .map(specialiteMapper::jpaEntityToSpecialite)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
        log.info("Spécialité supprimée : id={}", id);
    }
}
