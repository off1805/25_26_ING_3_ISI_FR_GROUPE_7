package com.projetTransversalIsi.Niveau.infrastructure;

import com.projetTransversalIsi.Filiere.infrastructure.FiliereMapper;
import com.projetTransversalIsi.Niveau.domain.Niveau;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FiliereMapper.class})
public interface NiveauMapper {

    @Mapping(target = "filiere", source = "filiere")
    Niveau toDomain(JpaNiveauEntity entity);

    @Mapping(target = "filiere", source = "filiere")
    JpaNiveauEntity toEntity(Niveau niveau);
}
