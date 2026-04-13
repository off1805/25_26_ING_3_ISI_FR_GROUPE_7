package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceRowEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PresenceRowMapper {
    JpaPresenceRowEntity toEntity(PresenceRow domain);
    PresenceRow toDomain(JpaPresenceRowEntity entity);
}
