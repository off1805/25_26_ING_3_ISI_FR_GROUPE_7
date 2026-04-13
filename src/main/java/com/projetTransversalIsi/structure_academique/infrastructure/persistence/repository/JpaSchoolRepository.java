package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.domain.model.School;
import com.projetTransversalIsi.structure_academique.domain.repository.SchoolRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSchoolEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper.SchoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaSchoolRepository implements SchoolRepository {

    private final SpringDataSchoolRepository springData;
    private final SchoolMapper schoolMapper;

    @Override
    public Optional<School> findById(Long id) {
        return springData.findById(id).map(schoolMapper::toDomain);
    }

    @Override
    public School save(School school) {
        JpaSchoolEntity entity = schoolMapper.toEntity(school);
        JpaSchoolEntity saved = springData.save(entity);
        return schoolMapper.toDomain(saved);
    }

    @Override
    public boolean existsById(Long id) {
        return springData.existsById(id);
    }
}
