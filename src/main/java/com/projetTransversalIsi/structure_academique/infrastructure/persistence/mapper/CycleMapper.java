package com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper;

import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaCycleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CycleMapper {

    @org.mapstruct.Mapping(target = "filieres", ignore = true)
    JpaCycleEntity cycleToJpaCycleEntity(Cycle cycle);

    Cycle jpaCycleEntityToCycle(JpaCycleEntity entity);
}
