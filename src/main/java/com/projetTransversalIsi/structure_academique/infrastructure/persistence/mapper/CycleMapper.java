package com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper;

import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaCycleEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSchoolEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CycleMapper {

    @Mapping(target = "filieres", ignore = true)
    @Mapping(target = "school", ignore = true)
    JpaCycleEntity cycleToJpaCycleEntity(Cycle cycle);

    @Mapping(target = "schoolId", source = "school", qualifiedByName = "schoolToId")
    Cycle jpaCycleEntityToCycle(JpaCycleEntity entity);

    @Named("schoolToId")
    default Long schoolToId(JpaSchoolEntity school) {
        return school == null ? null : school.getId();
    }
}
