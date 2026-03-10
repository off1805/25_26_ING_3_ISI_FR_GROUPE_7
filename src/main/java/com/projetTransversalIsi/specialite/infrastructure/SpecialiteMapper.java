package com.projetTransversalIsi.specialite.infrastructure;

import com.projetTransversalIsi.specialite.domain.Specialite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpecialiteMapper {

    JpaSpecialiteEntity specialiteToJpaEntity(Specialite specialite);

    Specialite jpaEntityToSpecialite(JpaSpecialiteEntity entity);
}
