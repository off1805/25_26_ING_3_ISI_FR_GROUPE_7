package com.projetTransversalIsi.specialite.infrastructure;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.classe.infrastructure.ClasseMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", 
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = {
            com.projetTransversalIsi.Niveau.infrastructure.NiveauMapper.class,
            ClasseMapper.class
        })
public interface SpecialiteMapper {

    @Mapping(target = "classes", source = "classes")
    JpaSpecialiteEntity specialiteToJpaEntity(Specialite specialite);

    @Mapping(target = "classes", source = "classes")
    Specialite jpaEntityToSpecialite(JpaSpecialiteEntity entity);
}
