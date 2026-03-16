package com.projetTransversalIsi.seance.infrastructure;

import com.projetTransversalIsi.seance.domain.Seance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeanceMapper {

    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    Seance toDomain(JpaSeanceEntity entity);

    JpaSeanceEntity toEntity(Seance seance);
}
