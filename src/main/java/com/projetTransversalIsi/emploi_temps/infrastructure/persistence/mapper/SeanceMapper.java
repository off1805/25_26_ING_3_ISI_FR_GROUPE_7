package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaSeanceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeanceMapper {

    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    Seance toDomain(JpaSeanceEntity entity);

    JpaSeanceEntity toEntity(Seance seance);
}
