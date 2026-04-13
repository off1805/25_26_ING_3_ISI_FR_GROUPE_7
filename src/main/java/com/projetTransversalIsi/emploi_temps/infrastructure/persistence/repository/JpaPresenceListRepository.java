package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.PresenceListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaPresenceListRepository implements PresenceListRepository {

    private final SpringDataPresenceListRepository springData;
    private final PresenceListMapper mapper;

    @Override
    public PresenceList save(PresenceList presenceList) {
        return mapper.toDomain(springData.save(mapper.toEntity(presenceList)));
    }

    @Override
    public Optional<PresenceList> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PresenceList> findBySeanceId(Long seanceId) {
        return springData.findBySeanceId(seanceId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PresenceList> findByClasseId(Long classeId) {
        return springData.findByClasseId(classeId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(PresenceList presenceList) {
        springData.save(mapper.toEntity(presenceList));
    }
}
