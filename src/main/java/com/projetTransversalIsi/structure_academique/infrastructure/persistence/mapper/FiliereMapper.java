package com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper;

import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaFiliereEntity;
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
