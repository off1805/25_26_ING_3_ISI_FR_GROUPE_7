package com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper;

import com.projetTransversalIsi.structure_academique.domain.model.School;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSchoolEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SchoolMapper {

    School toDomain(JpaSchoolEntity entity);

    JpaSchoolEntity toEntity(School domain);
}
