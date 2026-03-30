package com.projetTransversalIsi.structure_academique.infrastructure.persistence.mapper;

import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSpecialiteEntity;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", 
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = {
            NiveauMapper.class,
            FiliereMapper.class
        })
public interface SpecialiteMapper {

    @Mapping(target = "classes", ignore = true)
    JpaSpecialiteEntity specialiteToJpaEntity(Specialite specialite);

    Specialite jpaEntityToSpecialite(JpaSpecialiteEntity entity);
}
