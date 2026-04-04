package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.PresenceRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaPresenceRowRepository implements PresenceRowRepository {

    private final SpringDataPresenceRowRepository springData;
    private final PresenceRowMapper mapper;

    @Override
    public PresenceRow save(PresenceRow presenceRow) {
        return mapper.toDomain(springData.save(mapper.toEntity(presenceRow)));
    }

    @Override
    public Optional<PresenceRow> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PresenceRow> findByPresenceListId(Long presenceListId) {
        return springData.findByPresenceListId(presenceListId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PresenceRow> findByEtudiantId(Long etudiantId) {
        return springData.findByEtudiantId(etudiantId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(PresenceRow presenceRow) {
        springData.deleteById(presenceRow.getId());
    }
}
