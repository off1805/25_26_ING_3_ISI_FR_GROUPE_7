package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.Appel;
import com.projetTransversalIsi.emploi_temps.domain.repository.AppelRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.AppelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaAppelRepository implements AppelRepository {

    private final SpringDataAppelRepository springData;
    private final AppelMapper mapper;

    @Override
    public Appel save(Appel appel) {
        return mapper.toDomain(springData.save(mapper.toEntity(appel)));
    }

    @Override
    public Optional<Appel> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Appel> findByPresenceListId(Long presenceListId) {
        return springData.findByPresenceListId(presenceListId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Appel> findByAttendanceCodeId(Long attendanceCodeId) {
        return springData.findByAttendanceCodeId(attendanceCodeId).map(mapper::toDomain);
    }

    @Override
    public void delete(Long id) {
        springData.deleteById(id);
    }
}
