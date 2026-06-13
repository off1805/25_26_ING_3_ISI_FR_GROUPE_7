package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardRow;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaRetardRowEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RetardRowMapper {
    JpaRetardRowEntity toEntity(RetardRow domain);
    RetardRow toDomain(JpaRetardRowEntity entity);
}
