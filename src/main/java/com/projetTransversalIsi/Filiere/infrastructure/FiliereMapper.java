package com.projetTransversalIsi.Filiere.infrastructure;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FiliereMapper {

    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    Filiere toDomain(JpaFiliereEntity entity);

    JpaFiliereEntity toEntity(Filiere filiere);
}
