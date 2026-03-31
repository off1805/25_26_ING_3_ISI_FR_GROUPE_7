package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.domain.model.Classe;
import com.projetTransversalIsi.structure_academique.domain.repository.ClasseRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper.ClasseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaClasseRepository implements ClasseRepository {

    private final SpringDataClasseRepository jpaRepo;
    private final ClasseMapper mapper;

    @Override
    public Classe save(Classe classe) {
        JpaClasseEntity entity = mapper.toEntity(classe);
        JpaClasseEntity saved = jpaRepo.save(entity);
        log.info("Classe sauvegardée : id={}, code={}", saved.getId(), saved.getCode());
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Classe> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Classe> findByCode(String code) {
        return jpaRepo.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<Classe> findAll() {
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
        log.info("Classe supprimée : id={}", id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepo.existsByCode(code);
    }

    @Override
    public List<Classe> findBySpecialiteId(Long specialiteId) {
        return jpaRepo.findBySpecialiteId(specialiteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
