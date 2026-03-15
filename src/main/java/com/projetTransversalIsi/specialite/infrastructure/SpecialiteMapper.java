package com.projetTransversalIsi.specialite.infrastructure;

import com.projetTransversalIsi.specialite.domain.Specialite;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", 
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = {
            com.projetTransversalIsi.Niveau.infrastructure.NiveauMapper.class,
            com.projetTransversalIsi.Filiere.infrastructure.FiliereMapper.class
        })
public interface SpecialiteMapper {

    @Mapping(target = "classes", ignore = true)
    JpaSpecialiteEntity specialiteToJpaEntity(Specialite specialite);

    Specialite jpaEntityToSpecialite(JpaSpecialiteEntity entity);
}
