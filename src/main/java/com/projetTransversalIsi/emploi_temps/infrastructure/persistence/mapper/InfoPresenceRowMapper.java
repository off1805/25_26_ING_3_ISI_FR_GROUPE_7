package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaInfoPresenceRowEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InfoPresenceRowMapper {
    JpaInfoPresenceRowEntity toEntity(InfoPresenceRow domain);
    InfoPresenceRow toDomain(JpaInfoPresenceRowEntity entity);
}
