package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceListEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PresenceListMapper {
    JpaPresenceListEntity toEntity(PresenceList domain);
    PresenceList toDomain(JpaPresenceListEntity entity);
}
