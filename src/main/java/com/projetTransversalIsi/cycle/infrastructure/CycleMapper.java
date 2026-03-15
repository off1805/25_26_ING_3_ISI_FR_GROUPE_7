package com.projetTransversalIsi.cycle.infrastructure;

import com.projetTransversalIsi.cycle.domain.Cycle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CycleMapper {

    @org.mapstruct.Mapping(target = "filieres", ignore = true)
    JpaCycleEntity cycleToJpaCycleEntity(Cycle cycle);

    Cycle jpaCycleEntityToCycle(JpaCycleEntity entity);
}
