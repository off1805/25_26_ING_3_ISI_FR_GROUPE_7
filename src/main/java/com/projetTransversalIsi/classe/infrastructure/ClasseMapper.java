package com.projetTransversalIsi.classe.infrastructure;

import com.projetTransversalIsi.Filiere.infrastructure.FiliereMapper;
import com.projetTransversalIsi.Niveau.infrastructure.NiveauMapper;
import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.infrastructure.JpaSpecialiteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {FiliereMapper.class})
public interface ClasseMapper {

    @Mapping(target = "specialite", source = "specialite", qualifiedByName = "jpaToDomainSimple")
    Classe toDomain(JpaClasseEntity entity);

    @Mapping(target = "specialite", source = "specialite", qualifiedByName = "domainToJpaSimple")
    JpaClasseEntity toEntity(Classe domain);

    @Named("jpaToDomainSimple")
    default Specialite jpaToDomainSimple(JpaSpecialiteEntity entity) {
        if (entity == null) return null;
        Specialite domain = new Specialite();
        domain.setId(entity.getId());
        domain.setCode(entity.getCode());
        domain.setLibelle(entity.getLibelle());
        domain.setDescription(entity.getDescription());
        domain.setActive(entity.isActive());
        return domain;
    }

    @Named("domainToJpaSimple")
    default JpaSpecialiteEntity domainToJpaSimple(Specialite domain) {
        if (domain == null) return null;
        JpaSpecialiteEntity entity = new JpaSpecialiteEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
