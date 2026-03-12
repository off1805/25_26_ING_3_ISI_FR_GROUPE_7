package com.projetTransversalIsi.classe.infrastructure;

import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.infrastructure.JpaSpecialiteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ClasseMapper {

    @Mapping(target = "specialite", source = "specialite", qualifiedByName = "jpaToDomainSimple")
    Classe toDomain(JpaClasseEntity entity);

    @Mapping(target = "specialite", source = "specialite")
    JpaClasseEntity toEntity(Classe domain);

    @Named("jpaToDomainSimple")
    default Specialite jpaToDomainSimple(JpaSpecialiteEntity entity) {
        if (entity == null) return null;
        Specialite domain = new Specialite();
        domain.setId(entity.getId());
        domain.setCode(entity.getCode());
        domain.setDescription(entity.getDescription());
        domain.setActive(entity.isActive());
        return domain;
    }
}
