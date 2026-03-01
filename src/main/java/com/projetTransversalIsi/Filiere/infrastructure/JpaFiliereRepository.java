package com.projetTransversalIsi.Filiere.infrastructure;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaFiliereRepository implements FiliereRepository {

    private final SpringDataFiliereRepository jpaRepo;
    private final FiliereMapper mapper;

    @Override
    public Filiere save(Filiere filiere) {
        JpaFiliereEntity entity = mapper.toEntity(filiere);
        JpaFiliereEntity savedEntity = jpaRepo.save(entity);
        log.info("Filière sauvegardée: {}", savedEntity.getCode());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Filiere> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Filiere> findAll() {
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Filiere filiere) {
        jpaRepo.deleteById(filiere.getId());
    }

    @Override
    public Optional<Filiere> findActiveById(Long id) {
        return jpaRepo.findByIdAndDeletedFalse(id).map(mapper::toDomain);
    }

    @Override
    public List<Filiere> findAllActive() {
        return jpaRepo.findByDeletedFalse().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Filiere> findAllDeleted() {
        return jpaRepo.findByDeletedTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Filiere> findByCode(String code) {
        return jpaRepo.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<Filiere> searchByNom(String nom) {
        return jpaRepo.findByNomContainingIgnoreCase(nom).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepo.existsByCode(code);
    }

    @Override
    public boolean existsActiveByCode(String code) {
        return jpaRepo.existsByCodeAndDeletedFalse(code);
    }
}
