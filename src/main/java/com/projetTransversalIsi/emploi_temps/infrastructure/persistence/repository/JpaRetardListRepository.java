package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardList;
import com.projetTransversalIsi.emploi_temps.domain.repository.RetardListRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.RetardListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaRetardListRepository implements RetardListRepository {

    private final SpringDataRetardListRepository springData;
    private final RetardListMapper mapper;

    @Override
    public RetardList save(RetardList retardList) {
        return mapper.toDomain(springData.save(mapper.toEntity(retardList)));
    }

    @Override
    public Optional<RetardList> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<RetardList> findByClasseIdAndSemaineDebutAndDeletedFalse(Long classeId, LocalDate semaineDebut) {
        return springData.findByClasseIdAndSemaineDebutAndDeletedFalse(classeId, semaineDebut).map(mapper::toDomain);
    }

    @Override
    public List<RetardList> findByClasseId(Long classeId) {
        return springData.findByClasseId(classeId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(RetardList retardList) {
        springData.save(mapper.toEntity(retardList));
    }
}
