package com.projetTransversalIsi.emploi_Temps.infrastructure;

import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.seance.infrastructure.SeanceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SeanceMapper.class})
public interface EmploiTempsMapper {

    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "deletedAt", source = "deletedAt")
    @Mapping(target = "seances", source = "seances")
    EmploiTemps toDomain(JpaEmploiTempsEntity entity);

    @Mapping(target = "seances", source = "seances")
    JpaEmploiTempsEntity toEntity(EmploiTemps emploiTemps);
}