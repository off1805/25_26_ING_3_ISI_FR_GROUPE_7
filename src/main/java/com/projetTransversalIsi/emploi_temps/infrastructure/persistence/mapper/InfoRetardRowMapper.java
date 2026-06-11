package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoRetardRow;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaInfoRetardRowEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InfoRetardRowMapper {
    JpaInfoRetardRowEntity toEntity(InfoRetardRow domain);
    InfoRetardRow toDomain(JpaInfoRetardRowEntity entity);
}
