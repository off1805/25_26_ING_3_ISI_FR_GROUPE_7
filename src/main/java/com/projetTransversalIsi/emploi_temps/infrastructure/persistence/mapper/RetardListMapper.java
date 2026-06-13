package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardList;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaRetardListEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RetardListMapper {
    JpaRetardListEntity toEntity(RetardList domain);
    RetardList toDomain(JpaRetardListEntity entity);
}
