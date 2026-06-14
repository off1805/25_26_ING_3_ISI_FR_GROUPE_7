package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.InfoPresenceRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaInfoPresenceRowRepository implements InfoPresenceRowRepository {

    private final SpringDataInfoPresenceRowRepository springData;
    private final InfoPresenceRowMapper mapper;

    @Override
    public InfoPresenceRow save(InfoPresenceRow row) {
        return mapper.toDomain(springData.save(mapper.toEntity(row)));
    }

    @Override
    public Optional<InfoPresenceRow> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<InfoPresenceRow> findByPresenceRowId(Long presenceRowId) {
        return springData.findByPresenceRowId(presenceRowId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<InfoPresenceRow> findByPresenceRowIdIn(List<Long> presenceRowIds) {
        return springData.findByPresenceRowIdIn(presenceRowIds).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<InfoPresenceRow> findByAppelId(Long appelId) {
        return springData.findByAppelId(appelId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<InfoPresenceRow> findByAppelIdAndIsPresentIsNull(Long appelId) {
        return springData.findByAppelIdAndIsPresentIsNull(appelId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Long> findDistinctAppelIdsWithPendingRows() {
        return springData.findDistinctAppelIdsWithPendingRows();
    }

    @Override
    public void delete(Long id) {
        springData.deleteById(id);
    }
}
