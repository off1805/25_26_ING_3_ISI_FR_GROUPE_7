package com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper;

import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FiliereMapper.class})
public interface NiveauMapper {

    @Mapping(target = "filiere", source = "filiere")
    Niveau toDomain(JpaNiveauEntity entity);

    @Mapping(target = "filiere", source = "filiere")
    JpaNiveauEntity  toEntity(Niveau niveau);
}
