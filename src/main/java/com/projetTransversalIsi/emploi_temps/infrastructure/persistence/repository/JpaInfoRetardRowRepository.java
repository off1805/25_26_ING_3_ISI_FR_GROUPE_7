package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoRetardRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoRetardRowRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.InfoRetardRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaInfoRetardRowRepository implements InfoRetardRowRepository {

    private final SpringDataInfoRetardRowRepository springData;
    private final InfoRetardRowMapper mapper;

    @Override
    public InfoRetardRow save(InfoRetardRow row) {
        return mapper.toDomain(springData.save(mapper.toEntity(row)));
    }

    @Override
    public Optional<InfoRetardRow> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<InfoRetardRow> findByRetardRowId(Long retardRowId) {
        return springData.findByRetardRowId(retardRowId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<InfoRetardRow> findByRetardRowIdIn(List<Long> retardRowIds) {
        return springData.findByRetardRowIdIn(retardRowIds).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(InfoRetardRow row) {
        springData.deleteById(row.getId());
    }
}
