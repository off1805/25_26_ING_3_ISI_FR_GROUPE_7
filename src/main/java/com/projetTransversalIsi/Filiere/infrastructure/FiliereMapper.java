package com.projetTransversalIsi.Filiere.infrastructure;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.cycle.infrastructure.CycleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CycleMapper.class})
public interface FiliereMapper {

    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "cycle", source = "cycle")
    Filiere toDomain(JpaFiliereEntity entity);

    @Mapping(target = "cycle", source = "cycle")
    JpaFiliereEntity toEntity(Filiere filiere);
}
