package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.RetardRowRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.RetardRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaRetardRowRepository implements RetardRowRepository {

    private final SpringDataRetardRowRepository springData;
    private final RetardRowMapper mapper;

    @Override
    public RetardRow save(RetardRow retardRow) {
        return mapper.toDomain(springData.save(mapper.toEntity(retardRow)));
    }

    @Override
    public Optional<RetardRow> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<RetardRow> findByRetardListId(Long retardListId) {
        return springData.findByRetardListId(retardListId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(RetardRow retardRow) {
        springData.deleteById(retardRow.getId());
    }
}
