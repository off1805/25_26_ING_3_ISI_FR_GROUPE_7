package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper.AttendanceCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaAttendanceCodeRepository implements AttendanceCodeRepository {

    private final SpringDataAttendanceCodeRepository springData;
    private final AttendanceCodeMapper mapper;

    @Override
    public AttendanceCode save(AttendanceCode code) {
        return mapper.toDomain(springData.save(mapper.toEntity(code)));
    }

    @Override
    public Optional<AttendanceCode> findById(Long id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AttendanceCode> findBySeanceId(Long seanceId) {
        return springData.findBySeanceId(seanceId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceCode> findByEnseignantId(Long enseignantId) {
        return springData.findByEnseignantId(enseignantId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        springData.deleteById(id);
    }
}
