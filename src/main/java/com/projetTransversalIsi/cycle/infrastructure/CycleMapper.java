package com.projetTransversalIsi.cycle.infrastructure;

import com.projetTransversalIsi.cycle.domain.Cycle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CycleMapper {

    JpaCycleEntity cycleToJpaCycleEntity(Cycle cycle);

    Cycle jpaCycleEntityToCycle(JpaCycleEntity entity);
}
