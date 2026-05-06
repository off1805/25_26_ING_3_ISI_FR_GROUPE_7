package com.projetTransversalIsi.justificatif.infrastructure.mapper;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.infrastructure.entity.JpaJustificatifEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JustificatifMapper {
    JpaJustificatifEntity toEntity(Justificatif domain);
    Justificatif toDomain(JpaJustificatifEntity entity);
}
