package com.projetTransversalIsi.justificatif.infrastructure.repository;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.repository.JustificatifRepository;
import com.projetTransversalIsi.justificatif.infrastructure.mapper.JustificatifMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaJustificatifRepository implements JustificatifRepository {

    private final SpringDataJustificatifRepository springData;
    private final JustificatifMapper mapper;

    @Override
    public Justificatif save(Justificatif justificatif) {
        return mapper.toDomain(springData.save(mapper.toEntity(justificatif)));
    }

    @Override
    public Optional<Justificatif> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Justificatif> findByEtudiantId(Long etudiantId) {
        return springData.findByEtudiantId(etudiantId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Justificatif> findAll() {
        return springData.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        springData.deleteById(id);
    }
}
